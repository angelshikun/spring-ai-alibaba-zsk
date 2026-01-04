# ShellTool 使用示例

本示例展示了如何在 Spring AI Alibaba 项目中使用 ShellTool 来执行 Shell 命令。

## 项目简介

ShellTool 是 Spring AI Alibaba Agent Framework 提供的一个工具，允许在持久化的 Shell 会话中执行命令。本示例演示了 ShellTool 的各种配置和使用方式。

## 功能特性

- **基本命令执行**：执行简单的 Shell 命令
- **自定义配置**：配置超时时间、输出行数限制等
- **环境变量支持**：为 Shell 会话设置自定义环境变量
- **生命周期管理**：配置启动和关闭命令
- **输出控制**：限制输出行数，防止输出过大

## 快速开始

### 前置条件

- JDK 17 或更高版本
- Maven 3.6+

### 运行示例

1. 进入项目目录：
```bash
cd examples/shelluse
```

2. 编译项目：
```bash
mvn clean package
```

3. 运行示例：
```bash
mvn spring-boot:run
```

或者直接运行主类：
```bash
java -jar target/shelluse-0.0.1-SNAPSHOT.jar
```

## 示例说明

### 示例1：基本使用

展示如何创建一个基本的 ShellTool 并执行简单命令：

```java
ToolCallback shellTool = ShellTool.builder(workspaceRoot)
    .withName("execute_shell")
    .build();
```

### 示例2：自定义配置

演示如何自定义 ShellTool 的配置：

```java
ToolCallback shellTool = ShellTool.builder(workspaceRoot)
    .withName("custom_shell")
    .withDescription("自定义的 Shell 工具")
    .withCommandTimeout(30000)  // 30秒超时
    .withMaxOutputLines(500)    // 最多500行输出
    .build();
```

### 示例3：环境变量

展示如何为 Shell 会话设置自定义环境变量：

```java
Map<String, String> customEnv = new HashMap<>();
customEnv.put("CUSTOM_VAR", "Hello from ShellTool");

ToolCallback shellTool = ShellTool.builder(workspaceRoot)
    .withEnvironment(customEnv)
    .build();
```

### 示例4：启动和关闭命令

演示如何配置会话启动和关闭时执行的命令：

```java
List<String> startupCommands = List.of(
    "echo Initializing session...",
    "export INIT_FLAG=true"
);

List<String> shutdownCommands = List.of(
    "echo Cleanup completed"
);

ToolCallback shellTool = ShellTool.builder(workspaceRoot)
    .withStartupCommands(startupCommands)
    .withShutdownCommands(shutdownCommands)
    .build();
```

### 示例5：超时和输出限制

展示输出截断和超时控制：

```java
ToolCallback shellTool = ShellTool.builder(workspaceRoot)
    .withCommandTimeout(5000)    // 5秒超时
    .withMaxOutputLines(10)      // 最多10行输出
    .build();
```

## ShellTool 配置参数

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| workspaceRoot | String | 必填 | Shell 会话的工作目录 |
| name | String | "shell" | 工具名称 |
| description | String | 默认描述 | 工具描述信息 |
| commandTimeout | long | 60000 | 命令超时时间（毫秒） |
| maxOutputLines | int | 1000 | 最大输出行数 |
| startupCommands | List\<String> | null | 会话启动时执行的命令 |
| shutdownCommands | List\<String> | null | 会话关闭时执行的命令 |
| shellCommand | List\<String> | null | 自定义 Shell 命令（如指定特定 Shell） |
| environment | Map\<String, String> | null | 自定义环境变量 |

## 执行命令的请求格式

ShellTool 接受以下参数：

```java
{
  "command": "要执行的命令",
  "restart": false  // 是否在执行前重启会话
}
```

## 使用场景

ShellTool 适用于以下场景：

1. **文件系统操作**：创建、删除、移动文件和目录
2. **系统信息获取**：获取系统状态、进程信息等
3. **脚本执行**：执行复杂的 Shell 脚本
4. **工具调用**：调用系统中安装的各种命令行工具
5. **AI Agent 集成**：作为 AI Agent 的工具，让 Agent 能够执行系统命令

## 注意事项

1. **安全性**：在生产环境中使用时，应该严格控制可执行的命令，避免安全风险
2. **跨平台**：注意不同操作系统的命令差异（Windows vs Linux/Mac）
3. **超时设置**：为长时间运行的命令设置合理的超时时间
4. **输出限制**：对可能产生大量输出的命令设置行数限制
5. **工作目录**：确保工作目录存在且有适当的权限

## 相关文档

- [Spring AI Alibaba Agent Framework](../../spring-ai-alibaba-agent-framework)
- [ShellTool 源码](../../spring-ai-alibaba-agent-framework/src/main/java/com/alibaba/cloud/ai/graph/agent/tools/ShellTool.java)
- [QoderChatModel 文档](QODER_CHAT_MODEL.md)
- [StreamingShellExample 流式输出指南](STREAMING_GUIDE.md)

## QoderChatAgentDemo 使用说明

### 功能特性

`QoderChatAgentDemo` 是一个基于 ReactAgent + QoderChatModel 的命令行多轮对话系统：

- **ReactAgent 集成**: 使用 Spring AI Alibaba 的 ReactAgent 框架
- **QoderChatModel 底层**: 内置 qodercli 命令行工具
- **多轮对话**: 支持上下文记忆的多轮对话
- **命令行交互**: 简洁的命令行界面
- **流式输出**: 可选的流式响应模式

### 运行示例

```bash
# 基本版本（同步响应）
java -cp target/shelluse-0.0.1-SNAPSHOT.jar \
     com.alibaba.cloud.ai.examples.shelluse.QoderChatAgentDemo

# 流式版本（实时输出）
java -cp target/shelluse-0.0.1-SNAPSHOT.jar \
     com.alibaba.cloud.ai.examples.shelluse.QoderChatAgentDemo streamingDemo
```

### 使用示例

```
=== Qoder Chat Agent Demo ===
基于 ReactAgent + QoderChatModel 的多轮对话系统
输入 'exit' 或 'quit' 退出程序

✓ QoderChatModel 初始化成功
✓ ReactAgent 初始化成功

开始对话...

You: 如何在Java中创建线程池？

[对话轮次: 1]
Agent: 在Java中创建线程池可以使用 ExecutorService...

You: 请给个代码示例

[对话轮次: 2]
Agent: 以下是一个示例...

You: exit

再见！
```

### 代码示例

```java
// 创建 QoderChatModel
QoderChatModel chatModel = QoderChatModel.builder()
    .executorService(executorService)
    .activeProcesses(activeProcesses)
    .workspace(Paths.get(System.getProperty("user.dir")))
    .timeoutSeconds(300)
    .build();

// 构建 ReactAgent
ReactAgent agent = ReactAgent.builder()
    .name("qoder_chat_agent")
    .model(chatModel)
    .description("基于 qodercli 的智能对话助手")
    .instruction("你是一个强大的 AI 编程助手...")
    .saver(new MemorySaver()) // 支持多轮对话
    .enableLogging(false)
    .build();

// 多轮对话
RunnableConfig config = RunnableConfig.builder()
    .threadId("session-id")
    .build();

AssistantMessage response = agent.call(userInput, config);
System.out.println(response.getText());
```

### 核心特性

1. **上下文记忆**: 使用 `MemorySaver` 保存对话历史
2. **会话管理**: 通过 `threadId` 管理不同会话
3. **资源管理**: 自动清理线程池和进程
4. **错误处理**: 完善的异常捕获和提示

## 许可证

Apache License 2.0
