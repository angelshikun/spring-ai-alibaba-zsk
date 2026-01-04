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

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.Consumer;

@Component
public class StreamingShellExample {

	/**
	 * 默认超时时间（秒）
	 */
	private static final long DEFAULT_TIMEOUT_SECONDS = 300;


	/**
	 * 线程池核心线程数
	 */
	private static final int CORE_POOL_SIZE = 10;

	/**
	 * 线程池最大线程数
	 */
	private static final int MAX_POOL_SIZE = 15;

	/**
	 * 线程空闲存活时间（秒）
	 */
	private static final long KEEP_ALIVE_TIME = 60;

	/**
	 * 输出读取线程池
	 */
	private final ExecutorService readerThreadPool;

	/**
	 * 活跃进程跟踪集合（用于优雅关闭）
	 */
	private final Set<Process> activeProcesses;

	/**
	 * 构造函数 - 初始化线程池和进程管理
	 */
	public StreamingShellExample() {
		// 创建线程池，使用有界队列防止内存溢出
		this.readerThreadPool = new ThreadPoolExecutor(
				CORE_POOL_SIZE,
				MAX_POOL_SIZE,
				KEEP_ALIVE_TIME,
				TimeUnit.SECONDS,
				new LinkedBlockingQueue<>(100),
				new ThreadFactory() {
					private final java.util.concurrent.atomic.AtomicInteger counter = 
							new java.util.concurrent.atomic.AtomicInteger(0);

					@Override
					public Thread newThread(Runnable r) {
						Thread thread = new Thread(r, "streaming-reader-" + counter.incrementAndGet());
						thread.setDaemon(false); // 非守护线程，确保任务完成
						return thread;
					}
				},
				new ThreadPoolExecutor.AbortPolicy() // 拒绝策略
		);

		// 使用线程安全的Set跟踪活跃进程
		this.activeProcesses = ConcurrentHashMap.newKeySet();
	}

	/**
	 * 优雅关闭 - Spring容器销毁时调用
	 */
	@PreDestroy
	public void shutdown() {
		System.out.println("开始优雅关闭 StreamingShellExample...");

		// 1. 关闭线程池，不再接受新任务
		readerThreadPool.shutdown();

		try {
			// 2. 等待现有任务完成（最多30秒）
			if (!readerThreadPool.awaitTermination(30, TimeUnit.SECONDS)) {
				System.err.println("线程池未能在30秒内完成，强制关闭");
				readerThreadPool.shutdownNow();
			}
		} catch (InterruptedException e) {
			readerThreadPool.shutdownNow();
			Thread.currentThread().interrupt();
		}

		// 3. 清理所有活跃进程
		cleanupAllProcesses();

		System.out.println("StreamingShellExample 已优雅关闭");
	}

	/**
	 * 获取线程池（用于其他组件复用）
	 */
	public ExecutorService getReaderThreadPool() {
		return readerThreadPool;
	}

	/**
	 * 获取活跃进程集合（用于其他组件复用）
	 */
	public Set<Process> getActiveProcesses() {
		return activeProcesses;
	}

	/**
	 * 清理所有活跃进程
	 */
	private void cleanupAllProcesses() {
		if (activeProcesses.isEmpty()) {
			return;
		}

		System.out.println("正在清理 " + activeProcesses.size() + " 个活跃进程...");

		for (Process process : activeProcesses) {
			if (process.isAlive()) {
				process.destroyForcibly();
			}
		}

		activeProcesses.clear();
	}

	/**
	 * 获取线程池状态信息（用于监控）
	 */
	public PoolStats getPoolStats() {
		if (readerThreadPool instanceof ThreadPoolExecutor) {
			ThreadPoolExecutor executor = (ThreadPoolExecutor) readerThreadPool;
			return new PoolStats(
					executor.getActiveCount(),
					executor.getPoolSize(),
					executor.getQueue().size(),
					executor.getCompletedTaskCount(),
					activeProcesses.size()
			);
		}
		return new PoolStats(0, 0, 0, 0, activeProcesses.size());
	}

	/**
	 * 线程池统计信息
	 */
	public static class PoolStats {
		private final int activeThreads;
		private final int poolSize;
		private final int queuedTasks;
		private final long completedTasks;
		private final int activeProcesses;

		public PoolStats(int activeThreads, int poolSize, int queuedTasks, 
		                 long completedTasks, int activeProcesses) {
			this.activeThreads = activeThreads;
			this.poolSize = poolSize;
			this.queuedTasks = queuedTasks;
			this.completedTasks = completedTasks;
			this.activeProcesses = activeProcesses;
		}

		public int getActiveThreads() {
			return activeThreads;
		}

		public int getPoolSize() {
			return poolSize;
		}

		public int getQueuedTasks() {
			return queuedTasks;
		}

		public long getCompletedTasks() {
			return completedTasks;
		}

		public int getActiveProcesses() {
			return activeProcesses;
		}

		@Override
		public String toString() {
			return String.format(
					"PoolStats{activeThreads=%d, poolSize=%d, queuedTasks=%d, " +
							"completedTasks=%d, activeProcesses=%d}",
					activeThreads, poolSize, queuedTasks, completedTasks, activeProcesses
			);
		}
	}

	/**
	 * 流式执行 Shell 命令
	 *
	 * @param command 要执行的命令
	 * @param workspace 工作目录
	 * @param outputConsumer 输出消费者，每产生一行输出就调用一次（可为null）
	 * @return 异步结果 Future，调用者可自行决定何时获取退出码
	 */
	public Future<Integer> executeStreamingCommand(String command, Path workspace, Consumer<String> outputConsumer) {
		return executeStreamingCommand(command, workspace, outputConsumer, DEFAULT_TIMEOUT_SECONDS);
	}

	/**
	 * 流式执行 Shell 命令（带自定义超时）
	 *
	 * @param command 要执行的命令
	 * @param workspace 工作目录
	 * @param outputConsumer 输出消费者，每产生一行输出就调用一次（可为null）
	 * @param timeoutSeconds 超时时间（秒）
	 * @return 异步结果 Future，调用者可自行决定何时获取退出码
	 */
	public Future<Integer> executeStreamingCommand(String command, Path workspace, 
	                                               Consumer<String> outputConsumer, long timeoutSeconds) {
		// 创建并提交进程任务
		ProcessTask task = new ProcessTask(command, workspace, outputConsumer, timeoutSeconds, activeProcesses);
		return readerThreadPool.submit(task);
	}
	
	/**
	 * 流式执行命令(带超时控制)
	 * 该方法主要用于统一返回结果封装
	 *
	 * @param command 要执行的命令
	 * @param workspace 工作目录
	 * @param outputConsumer 输出消费者
	 * @param timeoutMs 超时时间(毫秒)
	 * @return 执行结果
	 */
	public StreamingResult executeStreamingCommandWithTimeout(
			String command,
			Path workspace,
			Consumer<String> outputConsumer,
			long timeoutMs) {
	
		try {
			long timeoutSeconds = timeoutMs / 1000;
			// 提交异步任务
			Future<Integer> future = executeStreamingCommand(command, workspace, outputConsumer, timeoutSeconds);
			// 等待结果（带额外缓冲）
			int exitCode = future.get(timeoutMs + 10000, TimeUnit.MILLISECONDS);
			return new StreamingResult(exitCode, false);
		} catch (TimeoutException e) {
			// 超时
			return new StreamingResult(-1, true);
		} catch (ExecutionException e) {
			// 执行异常
			Throwable cause = e.getCause();
			if (cause != null && cause.getMessage() != null && cause.getMessage().contains("超时")) {
				return new StreamingResult(-1, true);
			}
			return new StreamingResult(-1, false);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return new StreamingResult(-1, false);
		}
	}

	/**
	 * 流式执行结果
	 */
	public static class StreamingResult {
		private final int exitCode;
		private final boolean timedOut;

		public StreamingResult(int exitCode, boolean timedOut) {
			this.exitCode = exitCode;
			this.timedOut = timedOut;
		}

		public int getExitCode() {
			return exitCode;
		}

		public boolean isTimedOut() {
			return timedOut;
		}

		public boolean isSuccess() {
			return !timedOut && exitCode == 0;
		}
	}

	/**
	 * 演示：使用流式输出执行长时间命令
	 */
	public void demoStreamingExecution() throws ExecutionException, InterruptedException, TimeoutException {
		System.out.println("\n========== 流式输出演示 ==========\n");

		Path workspace = Path.of(System.getProperty("java.io.tmpdir"), "shell-streaming");
		workspace.toFile().mkdirs();

		// 示例1: 查看线程池初始状态
		System.out.println("【示例1】线程池初始状态");
		System.out.println("----------------------------------------");
		System.out.println(getPoolStats());
		System.out.println("----------------------------------------\n");

		// 示例2: 异步执行多个命令
		System.out.println("【示例2】异步执行 - 测试线程池");
		System.out.println("----------------------------------------");
		
		List<Future<Integer>> futures = new ArrayList<>();
		
		// 提交3个异步任务
		for (int i = 0; i < 3; i++) {
			final int index = i;
			Future<Integer> future = executeStreamingCommand(
					"echo 'Task " + index + "' && sleep 1 && echo 'Done'",
					workspace,
					line -> System.out.println("[Task-" + index + "] " + line)
			);
			futures.add(future);
			System.out.println("任务 " + index + " 已提交");
		}
		
		try {
			// 等待一点时间，观察线程池状态
			Thread.sleep(500);
			System.out.println("\n异步执行中的线程池状态: " + getPoolStats());
			
			// 等待所有任务完成
			for (int i = 0; i < futures.size(); i++) {
				try {
					int exitCode = futures.get(i).get(10, TimeUnit.SECONDS);
					System.out.println("任务 " + i + " 完成，退出码: " + exitCode);
				} catch (Exception e) {
					System.err.println("任务 " + i + " 失败: " + e.getMessage());
				}
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		
		System.out.println("\n并发任务完成后的线程池状态: " + getPoolStats());
		System.out.println("----------------------------------------\n");

		// 示例3: 带超时控制
		System.out.println("【示例3】流式执行 - 超时控制");
		System.out.println("----------------------------------------");

		StreamingResult result = executeStreamingCommandWithTimeout(
				"echo 'Starting...' && sleep 1 && echo 'Done'",
				workspace,
				line -> System.out.println("[Output] " + line),
				5000 // 5秒超时
		);

		if (result.isTimedOut()) {
			System.out.println("命令超时被中断");
		} else {
			System.out.println("命令正常完成，退出码: " + result.getExitCode());
		}
		System.out.println("----------------------------------------\n");
		
		// 最终状态 qodercli -p '简单介绍一下 Java 中的 Reactor'
		System.out.println("最终线程池状态: " + getPoolStats());

		Future<Integer> future = executeStreamingCommand(
				"qodercli -p '简单介绍一下 Java 中的 Reactor'",
				workspace,
				line -> System.out.println("[qodercli] " + line)
		);
		int exitCode = future.get(10, TimeUnit.SECONDS);
		System.out.println("[qodercli] exitCode：" + exitCode);
	}

	public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
		StreamingShellExample example = new StreamingShellExample();
		example.demoStreamingExecution();
	}
}
