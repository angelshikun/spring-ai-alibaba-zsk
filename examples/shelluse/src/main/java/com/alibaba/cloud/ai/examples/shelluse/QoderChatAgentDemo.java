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

import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import org.springframework.ai.chat.messages.AssistantMessage;

import java.nio.file.Paths;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Qoder Chat Agent Demo
 * <p>
 * 使用 ReactAgent + QoderChatModel 实现命令行多轮对话
 * </p>
 *
 * @author zsk
 */
public class QoderChatAgentDemo {

	private static final String THREAD_ID = "qoder-chat-session";
	
	public static void main(String[] args) {
		runStreamingDemo();
	}

	/**
	 * 交互式对话模式（同步响应）
	 */
	public static void runInteractiveDemo() {
		System.out.println("=== Qoder Chat Agent Demo ===");
		System.out.println("基于 ReactAgent + QoderChatModel 的多轮对话系统");
		System.out.println("输入 'exit' 或 'quit' 退出程序\n");

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
					Thread thread = new Thread(r, "qoder-agent-" + counter.incrementAndGet());
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

			System.out.println("✓ QoderChatModel 初始化成功");

			// 4. 构建 ReactAgent
			ReactAgent agent = ReactAgent.builder()
				.name("qoder_chat_agent")
				.model(chatModel)
				.description("Qoder AI assistant based on qodercli")
				.systemPrompt("You are a powerful AI programming assistant based on qodercli tool. " +
					"You can help users with: answering programming questions, generating code, " +
					"explaining code logic, and providing technical advice. " +
					"Please respond in a concise and professional manner.")
				.saver(new MemorySaver())
				.enableLogging(false)
				.build();

			System.out.println("✓ ReactAgent 初始化成功");
			System.out.println("\n开始对话...\n");

			// 5. 创建 RunnableConfig 用于多轮对话
			RunnableConfig config = RunnableConfig.builder()
				.threadId(THREAD_ID)
				.build();

			// 6. 命令行交互循环
			Scanner scanner = new Scanner(System.in);
			int turnCount = 0;

			while (true) {
				// 显示提示符
				System.out.print("You: ");
				String userInput = scanner.nextLine().trim();

				// 检查退出命令
				if (userInput.equalsIgnoreCase("exit") || 
				    userInput.equalsIgnoreCase("quit")) {
					System.out.println("\n再见！");
					break;
				}

				// 跳过空输入
				if (userInput.isEmpty()) {
					continue;
				}

				turnCount++;
				System.out.println("\n[对话轮次: " + turnCount + "]");
				System.out.print("Agent: ");

				try {
					// 调用 Agent
					AssistantMessage response = agent.call(userInput, config);
					
					// 输出响应
					System.out.println(response.getText());
					System.out.println();

				} catch (Exception e) {
					System.err.println("错误: " + e.getMessage());
					System.err.println("请重试或输入新的问题。\n");
				}
			}

			scanner.close();

		} catch (Exception e) {
			System.err.println("启动失败: " + e.getMessage());
			e.printStackTrace();
		} finally {
			// 7. 优雅关闭
			System.out.println("\n=== 清理资源 ===");
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
			System.out.println("✓ 所有资源已清理完成");
		}
	}

	/**
	 * 流式响应版本
	 */
	public static void runStreamingDemo() {
		System.out.println("=== Qoder Chat Agent Demo (流式版本) ===\n");

		ExecutorService executorService = new ThreadPoolExecutor(
			5, 10, 60, TimeUnit.SECONDS,
			new LinkedBlockingQueue<>(100),
			r -> new Thread(r, "qoder-streaming"),
			new ThreadPoolExecutor.AbortPolicy()
		);

		Set<Process> activeProcesses = ConcurrentHashMap.newKeySet();

		try {
			QoderChatModel chatModel = QoderChatModel.builder()
				.executorService(executorService)
				.activeProcesses(activeProcesses)
				.workspace(Paths.get(System.getProperty("user.dir")))
				.timeoutSeconds(300)
				.build();

			ReactAgent agent = ReactAgent.builder()
				.name("qoder_streaming_agent")
				.model(chatModel)
				.description("支持流式输出的 qodercli 助手")
				.instruction("你是一个 AI 编程助手，请简洁专业地回答问题。")
				.saver(new MemorySaver())
				.enableLogging(false)
				.build();

			RunnableConfig config = RunnableConfig.builder()
				.threadId("streaming-session")
				.build();

			Scanner scanner = new Scanner(System.in);

			while (true) {
				System.out.print("You: ");
				String input = scanner.nextLine().trim();

				if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
					break;
				}

				if (input.isEmpty()) {
					continue;
				}

				System.out.print("Agent: ");

				// 使用流式 API
				agent.stream(input, config)
					.subscribe(
						nodeOutput -> {
							// 获取状态中的消息
							nodeOutput.state().value("messages")
								.ifPresent(messages -> {
									if (messages instanceof java.util.List) {
										var list = (java.util.List<?>) messages;
										if (!list.isEmpty()) {
											var last = list.get(list.size() - 1);
											if (last instanceof AssistantMessage) {
												System.out.print(((AssistantMessage) last).getText());
											}
										}
									}
								});
						},
						error -> System.err.println("\n错误: " + error.getMessage()),
						() -> System.out.println("\n 对话结束")
					);

				// 等待流式响应完成
				Thread.sleep(1000);
			}

			scanner.close();

		} catch (Exception e) {
			System.err.println("错误: " + e.getMessage());
			e.printStackTrace();
		} finally {
			executorService.shutdown();
			activeProcesses.forEach(p -> {
				if (p.isAlive()) p.destroyForcibly();
			});
		}
	}
}
