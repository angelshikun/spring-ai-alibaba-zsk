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

## 许可证

Apache License 2.0
