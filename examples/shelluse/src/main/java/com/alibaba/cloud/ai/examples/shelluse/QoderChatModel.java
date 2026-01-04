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

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Flux;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Qoder CLI ChatModel 实现
 * <p>
 * 将 qodercli 命令行工具包装为 Spring AI ChatModel 接口
 * 使用 ProcessTask 执行命令并收集输出
 * </p>
 *
 * @author zsk
 */
public class QoderChatModel implements ChatModel {

	private static final int MAX_TURNS = 25;
	private static final long DEFAULT_TIMEOUT_SECONDS = 300; // 5分钟默认超时

	private final ExecutorService executorService;
	private final Set<Process> activeProcesses;
	private final Path workspace;
	private final long timeoutSeconds;

	/**
	 * 构造函数
	 *
	 * @param executorService 线程池
	 * @param activeProcesses 活跃进程集合
	 * @param workspace 工作目录
	 */
	public QoderChatModel(ExecutorService executorService, Set<Process> activeProcesses, Path workspace) {
		this(executorService, activeProcesses, workspace, DEFAULT_TIMEOUT_SECONDS);
	}

	/**
	 * 构造函数（带自定义超时）
	 *
	 * @param executorService 线程池
	 * @param activeProcesses 活跃进程集合
	 * @param workspace 工作目录
	 * @param timeoutSeconds 超时时间（秒）
	 */
	public QoderChatModel(ExecutorService executorService, Set<Process> activeProcesses, 
	                      Path workspace, long timeoutSeconds) {
		this.executorService = executorService;
		this.activeProcesses = activeProcesses;
		this.workspace = workspace;
		this.timeoutSeconds = timeoutSeconds;
	}

	@Override
	public ChatResponse call(Prompt prompt) {
		// 提取用户问题
		String userQuery = extractUserQuery(prompt);
		
		// 构建 qodercli 命令
		String command = buildQoderCommand(userQuery);
		
		// 收集输出
		StringBuilder outputCollector = new StringBuilder();
		
		// 创建 ProcessTask
		ProcessTask task = new ProcessTask(
			command, 
			workspace, 
			line -> {
				outputCollector.append(line).append("\n");
			}, 
			timeoutSeconds, 
			activeProcesses
		);
		
		// 提交任务并等待结果
		Future<Integer> future = executorService.submit(task);
		
		try {
			// 等待执行完成
			int exitCode = future.get(timeoutSeconds, TimeUnit.SECONDS);
			
			// 构建响应
			String responseText = outputCollector.toString();
			if (responseText.isEmpty()) {
				responseText = "命令执行完成，退出码: " + exitCode;
			}
			
			AssistantMessage assistantMessage = new AssistantMessage(responseText);
			Generation generation = new Generation(assistantMessage);
			
			return new ChatResponse(List.of(generation));
			
		} catch (TimeoutException e) {
			future.cancel(true);
			throw new RuntimeException("Qoder CLI 执行超时（" + timeoutSeconds + "秒）", e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			future.cancel(true);
			throw new RuntimeException("Qoder CLI 执行被中断", e);
		} catch (ExecutionException e) {
			throw new RuntimeException("Qoder CLI 执行失败: " + e.getMessage(), e);
		}
	}

	@Override
	public Flux<ChatResponse> stream(Prompt prompt) {
		return Flux.create(sink -> {
			// 提取用户问题
			String userQuery = extractUserQuery(prompt);
			
			// 构建 qodercli 命令
			String command = buildQoderCommand(userQuery);
			
			// 累积输出用于构建完整消息
			StringBuilder contentBuilder = new StringBuilder();
			
			// 创建 ProcessTask，每行输出发送一个流式响应
			ProcessTask task = new ProcessTask(
				command,
				workspace,
				line -> {
					// 累积内容
					if (contentBuilder.length() > 0) {
						contentBuilder.append("\n");
					}
					contentBuilder.append(line);
					
					// 发送流式chunk（只包含新增的行）
					AssistantMessage chunkMessage = new AssistantMessage(line + "\n");
					Generation generation = new Generation(chunkMessage);
					ChatResponse response = new ChatResponse(List.of(generation));
					
					sink.next(response);
				},
				timeoutSeconds,
				activeProcesses
			);
			
			try {
				// 直接调用 call() 方法执行任务
				int exitCode = task.call();
				
				// 如果没有任何输出，发送一个默认消息
				if (contentBuilder.length() == 0) {
					String message = "命令执行完成，退出码: " + exitCode;
					AssistantMessage finalMessage = new AssistantMessage(message);
					Generation generation = new Generation(finalMessage);
					ChatResponse response = new ChatResponse(List.of(generation));
					sink.next(response);
				}
				
				// 完成流
				sink.complete();
				
			} catch (Exception e) {
				// 处理所有异常
				sink.error(new RuntimeException("流式执行出错: " + e.getMessage(), e));
			}
		});
	}

	/**
	 * 从 Prompt 中提取用户查询问题
	 */
	private String extractUserQuery(Prompt prompt) {
		List<Message> messages = prompt.getInstructions();
		if (messages.isEmpty()) {
			throw new IllegalArgumentException("Prompt 不能为空");
		}
		
		// 查找最后一个 UserMessage
		for (int i = messages.size() - 1; i >= 0; i--) {
			Message message = messages.get(i);
			if (message instanceof UserMessage) {
				return message.getText();
			}
		}
		
		// 如果没有 UserMessage，使用第一个消息
		return messages.get(0).getText();
	}

	/**
	 * 构建 qodercli 命令
	 */
	private String buildQoderCommand(String userQuery) {
		// 转义引号
		String escapedQuery = userQuery.replace("\"", "\\\"");
		
		// 构建命令: qodercli -p "xxx" --max-turns 25
		return String.format("qodercli -p \"%s\" --max-turns %d", escapedQuery, MAX_TURNS);
	}

	/**
	 * 获取工作目录
	 */
	public Path getWorkspace() {
		return workspace;
	}

	/**
	 * 获取超时时间（秒）
	 */
	public long getTimeoutSeconds() {
		return timeoutSeconds;
	}

	/**
	 * Builder 模式构建器
	 */
	public static class Builder {
		private ExecutorService executorService;
		private Set<Process> activeProcesses;
		private Path workspace = Paths.get(System.getProperty("user.dir"));
		private long timeoutSeconds = DEFAULT_TIMEOUT_SECONDS;

		public Builder executorService(ExecutorService executorService) {
			this.executorService = executorService;
			return this;
		}

		public Builder activeProcesses(Set<Process> activeProcesses) {
			this.activeProcesses = activeProcesses;
			return this;
		}

		public Builder workspace(Path workspace) {
			this.workspace = workspace;
			return this;
		}

		public Builder workspace(String workspace) {
			this.workspace = Paths.get(workspace);
			return this;
		}

		public Builder timeoutSeconds(long timeoutSeconds) {
			this.timeoutSeconds = timeoutSeconds;
			return this;
		}

		public QoderChatModel build() {
			if (executorService == null) {
				throw new IllegalStateException("executorService 不能为 null");
			}
			if (activeProcesses == null) {
				// 如果未提供，创建一个新的
				activeProcesses = ConcurrentHashMap.newKeySet();
			}
			return new QoderChatModel(executorService, activeProcesses, workspace, timeoutSeconds);
		}
	}

	/**
	 * 创建 Builder
	 */
	public static Builder builder() {
		return new Builder();
	}
}
