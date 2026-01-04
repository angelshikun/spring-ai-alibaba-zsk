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

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;

import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * QoderChatModel 使用示例
 *
 * @author zsk
 */
public class QoderChatModelDemo {

	public static void main(String[] args) {
		// 1. 创建线程池
		ExecutorService executorService = new ThreadPoolExecutor(
			5,  // 核心线程数
			10, // 最大线程数
			60, // 空闲线程存活时间
			TimeUnit.SECONDS,
			new LinkedBlockingQueue<>(100),
			new ThreadFactory() {
				private final java.util.concurrent.atomic.AtomicInteger counter = 
					new java.util.concurrent.atomic.AtomicInteger(0);

				@Override
				public Thread newThread(Runnable r) {
					Thread thread = new Thread(r, "qoder-cli-" + counter.incrementAndGet());
					thread.setDaemon(false);
					return thread;
				}
			},
			new ThreadPoolExecutor.AbortPolicy()
		);

		// 2. 创建活跃进程集合
		Set<Process> activeProcesses = ConcurrentHashMap.newKeySet();

		try {
			// 3. 构建 QoderChatModel
			QoderChatModel chatModel = QoderChatModel.builder()
				.executorService(executorService)
				.activeProcesses(activeProcesses)
				.workspace(Paths.get(System.getProperty("user.dir")))
				.timeoutSeconds(300) // 5分钟超时
				.build();

			// 7. 使用示例 4: 流式响应
			System.out.println("=== 示例 4: 流式响应 ===");
			System.out.println("响应（实时流式）:");
			chatModel.stream(
				new Prompt(new UserMessage("用Java写一个计算斐波那契数列的程序"))
			).subscribe(
				response -> {
					// 实时打印每个chunk
					String chunk = response.getResult().getOutput().getText();
					System.out.print(chunk);
				},
				error -> {
					System.err.println("\n流式响应错误: " + error.getMessage());
				},
				() -> {
					System.out.println("\n[流式响应完成]");
				}
			);
			
			// 等待流式响应完成
			Thread.sleep(10000);
			System.out.println();

		} catch (Exception e) {
			System.err.println("执行失败: " + e.getMessage());
			e.printStackTrace();
		} finally {
			// 7. 优雅关闭
			System.out.println("=== 优雅关闭 ===");
			executorService.shutdown();
			try {
				if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
					System.err.println("线程池未能在30秒内完成，强制关闭");
					executorService.shutdownNow();
				}
			} catch (InterruptedException e) {
				executorService.shutdownNow();
				Thread.currentThread().interrupt();
			}

			// 清理所有活跃进程
			for (Process process : activeProcesses) {
				if (process.isAlive()) {
					process.destroyForcibly();
				}
			}
			executorService.shutdown();
			System.out.println("所有资源已清理完成");
		}
	}

//	/**
//	 * 演示如何集成到 Spring 环境
//	 */
//	public static class SpringIntegrationExample {
//
//		// 在 Spring 配置类中定义 Bean
//		// @Bean
//		public QoderChatModel qoderChatModel(StreamingShellExample shellExample) {
//			return QoderChatModel.builder()
//				.executorService(shellExample.getReaderThreadPool())
//				.activeProcesses(shellExample.getActiveProcesses())
//				.workspace(Paths.get(System.getProperty("user.dir")))
//				.timeoutSeconds(300)
//				.build();
//		}
//
//		// 使用示例
//		public void useQoderChatModel(QoderChatModel chatModel) {
//			ChatResponse response = chatModel.call(
//				new Prompt(new UserMessage("帮我分析这段代码的性能瓶颈"))
//			);
//			System.out.println(response.getResult().getOutput().getText());
//		}
//	}
}
