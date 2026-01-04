/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.cloud.ai.examples.shelluse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 进程执行任务
 * <p>
 * 封装了Shell命令进程的完整生命周期：创建、执行、输出读取、清理
 * 支持流式输出、超时控制、进程追踪等特性
 * </p>
 *
 * @author zsk
 */
public class ProcessTask implements Callable<Integer> {

	private final String command;

	private final Path workspace;

	private final Consumer<String> safeConsumer;

	private final long timeoutSeconds;

	private final ProcessBuilder processBuilder;

	private final Set<Process> activeProcesses;

	private Process process;

	/**
	 * 构造函数 - 初始化任务参数和ProcessBuilder
	 *
	 * @param command 要执行的Shell命令
	 * @param workspace 工作目录
	 * @param outputConsumer 输出消费者（可为null）
	 * @param timeoutSeconds 超时时间（秒）
	 * @param activeProcesses 活跃进程追踪集合
	 */
	public ProcessTask(String command, Path workspace, Consumer<String> outputConsumer, 
	                   long timeoutSeconds, Set<Process> activeProcesses) {
		this.command = command;
		this.workspace = workspace;
		this.safeConsumer = outputConsumer != null ? outputConsumer : (line) -> {};
		this.timeoutSeconds = timeoutSeconds;
		this.activeProcesses = activeProcesses;

		// 初始化 ProcessBuilder
		this.processBuilder = new ProcessBuilder("/bin/bash", "-c", command);
		this.processBuilder.directory(workspace.toFile());
		this.processBuilder.redirectErrorStream(true);
	}

	@Override
	public Integer call() throws Exception {
		try {
			// 1. 启动进程
			process = processBuilder.start();

			// 注册到活跃进程集合
			activeProcesses.add(process);

			// 2. 读取输出(带超时控制)
			readOutputWithTimeout();

			// 3. 等待进程完全结束并获取退出码
			return waitForProcessCompletion();

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("进程执行被中断", e);
		} catch (Exception e) {
			throw new RuntimeException("执行命令失败: " + e.getMessage(), e);
		} finally {
			// 4. 收尾工作：清理进程资源
			cleanup();
		}
	}

	/**
	 * 读取进程输出(带超时控制)
	 * <p>
	 * 采用非阻塞式轮询读取，每100ms检查一次超时和进程状态
	 * </p>
	 */
	private void readOutputWithTimeout() throws IOException, InterruptedException {
		long startTime = System.currentTimeMillis();
		long timeoutMillis = timeoutSeconds * 1000;

		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(process.getInputStream()))) {
			String line;

			// 循环读取,每次检查超时
			while (true) {
				// 检查是否超时
				long elapsed = System.currentTimeMillis() - startTime;
				if (elapsed > timeoutMillis) {
					process.destroyForcibly();
					throw new RuntimeException("进程执行超时（" + timeoutSeconds + "秒）");
				}

				// 检查进程是否已结束
				if (!process.isAlive()) {
					// 进程已结束,读取剩余输出后退出
					while ((line = reader.readLine()) != null) {
						try {
							safeConsumer.accept(line);
						} catch (Exception e) {
							// 消费者异常不应影响读取流程
						}
					}
					break;
				}

				// 尝试读取一行(非阻塞式检查)
				if (reader.ready()) {
					line = reader.readLine();
					if (line != null) {
						try {
							safeConsumer.accept(line);
						} catch (Exception e) {
							// 消费者异常不应影响读取流程
						}
					}
				} else {
					// 没有数据可读,短暂休眠避免CPU空转
					Thread.sleep(100);
				}
			}
		} catch (IOException e) {
			// 读取异常,通常是进程被销毁导致
			if (process.isAlive()) {
				throw new RuntimeException("读取进程输出失败", e);
			}
			// 进程已结束,正常情况
		}
	}

	/**
	 * 等待进程完全结束并获取退出码
	 */
	private int waitForProcessCompletion() throws InterruptedException {
		if (!process.waitFor(2, TimeUnit.SECONDS)) {
			process.destroyForcibly();
			process.waitFor(1, TimeUnit.SECONDS);
		}
		return process.exitValue();
	}

	/**
	 * 收尾工作：清理进程资源
	 * <p>
	 * 确保进程被强制销毁并从活跃进程集合中移除
	 * </p>
	 */
	private void cleanup() {
		if (process != null) {
			if (process.isAlive()) {
				process.destroyForcibly();
				try {
					process.waitFor(2, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			// 从活跃进程集合中移除
			activeProcesses.remove(process);
		}
	}

	/**
	 * 获取命令字符串
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * 获取工作目录
	 */
	public Path getWorkspace() {
		return workspace;
	}

}
