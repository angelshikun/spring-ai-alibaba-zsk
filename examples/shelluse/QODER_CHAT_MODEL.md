# QoderChatModel 架构设计

## 📋 概述

`QoderChatModel` 是一个将 `qodercli` 命令行工具包装为 Spring AI `ChatModel` 接口的实现类。它通过 `ProcessTask` 执行命令并收集输出,提供标准的对话式 AI 接口。

## 🏗️ 架构图

```mermaid
graph TB
    subgraph "Spring AI 接口层"
        ChatModel[ChatModel Interface]
        Prompt[Prompt]
        ChatResponse[ChatResponse]
    end
    
    subgraph "QoderChatModel 实现层"
        QCM[QoderChatModel]
        Builder[QoderChatModel.Builder]
        QCM -->|implements| ChatModel
        Builder -->|builds| QCM
    end
    
    subgraph "进程执行层"
        ProcessTask[ProcessTask]
        ThreadPool[ExecutorService]
        ActiveProcesses[Set&lt;Process&gt;]
        
        QCM -->|submits| ProcessTask
        ProcessTask -->|runs in| ThreadPool
        ProcessTask -->|tracks in| ActiveProcesses
    end
    
    subgraph "命令执行层"
        ProcessBuilder[ProcessBuilder]
        Process[Process]
        QoderCLI[qodercli命令]
        
        ProcessTask -->|creates| ProcessBuilder
        ProcessBuilder -->|starts| Process
        Process -->|executes| QoderCLI
    end
    
    User[用户] -->|creates| Prompt
    Prompt -->|calls| QCM
    QCM -->|returns| ChatResponse
    ChatResponse -->|to| User
```

## 🔧 核心组件

### 1. **QoderChatModel**
- **职责**: 实现 Spring AI `ChatModel` 接口
- **关键方法**:
  - `call(Prompt prompt)`: 同步执行命令并返回响应
  - `extractUserQuery(Prompt)`: 从 Prompt 提取用户问题
  - `buildQoderCommand(String)`: 构建 qodercli 命令

### 2. **ProcessTask**
- **职责**: 封装进程的完整生命周期
- **关键方法**:
  - `call()`: 启动进程、读取输出、等待结束、清理资源
  - `readOutputWithTimeout()`: 流式读取输出
  - `waitForProcessCompletion()`: 等待进程结束
  - `cleanup()`: 资源清理

### 3. **StreamingShellExample**
- **职责**: 提供线程池和进程追踪基础设施
- **对外接口**:
  - `getReaderThreadPool()`: 获取线程池
  - `getActiveProcesses()`: 获取活跃进程集合

## 📊 执行流程

### 同步调用流程

```mermaid
sequenceDiagram
    participant User
    participant QoderChatModel
    participant ProcessTask
    participant ThreadPool
    participant Process
    participant QoderCLI

    User->>QoderChatModel: call(Prompt)
    QoderChatModel->>QoderChatModel: extractUserQuery()
    QoderChatModel->>QoderChatModel: buildQoderCommand()
    QoderChatModel->>ProcessTask: new ProcessTask(command)
    QoderChatModel->>ThreadPool: submit(ProcessTask)
    
    ThreadPool->>ProcessTask: call()
    ProcessTask->>Process: processBuilder.start()
    Process->>QoderCLI: execute
    
    loop 读取输出
        QoderCLI-->>Process: output line
        Process-->>ProcessTask: readLine()
        ProcessTask->>ProcessTask: outputCollector.append()
    end
    
    QoderCLI-->>Process: exit
    Process-->>ProcessTask: exitValue()
    ProcessTask->>ProcessTask: cleanup()
    ProcessTask-->>ThreadPool: return exitCode
    ThreadPool-->>QoderChatModel: Future.get()
    
    QoderChatModel->>QoderChatModel: build ChatResponse
    QoderChatModel-->>User: return ChatResponse
```

### 流式调用流程

```mermaid
sequenceDiagram
    participant User
    participant QoderChatModel
    participant Flux
    participant ProcessTask
    participant Process
    participant QoderCLI

    User->>QoderChatModel: stream(Prompt)
    QoderChatModel->>Flux: Flux.create()
    QoderChatModel-->>User: return Flux
    
    Note over QoderChatModel,ProcessTask: 异步执行
    
    QoderChatModel->>ProcessTask: new ProcessTask(command)
    ProcessTask->>Process: start()
    Process->>QoderCLI: execute
    
    loop 实时输出
        QoderCLI-->>Process: output line
        Process-->>ProcessTask: readLine()
        ProcessTask->>Flux: sink.next(chunk)
        Flux-->>User: 实时接收 chunk
    end
    
    QoderCLI-->>Process: exit
    Process-->>ProcessTask: complete
    ProcessTask->>Flux: sink.complete()
    Flux-->>User: 完成信号
    
    alt 用户取消
        User->>Flux: cancel()
        Flux->>ProcessTask: future.cancel()
        ProcessTask->>Process: destroyForcibly()
    end
```

## 💡 使用示例

### 同步调用

```java
// 1. 创建 QoderChatModel
QoderChatModel chatModel = QoderChatModel.builder()
    .executorService(executorService)
    .activeProcesses(activeProcesses)
    .workspace(Paths.get(System.getProperty("user.dir")))
    .timeoutSeconds(300)
    .build();

// 2. 调用
ChatResponse response = chatModel.call(
    new Prompt(new UserMessage("如何在Java中创建线程池?"))
);

// 3. 获取结果
String answer = response.getResult().getOutput().getText();
System.out.println(answer);
```

### 流式调用

```java
// 流式响应 - 实时获取输出
chatModel.stream(
    new Prompt(new UserMessage("用Java写一个快速排序算法"))
).subscribe(
    response -> {
        // 实时打印每个chunk
        String chunk = response.getResult().getOutput().getText();
        System.out.print(chunk);
    },
    error -> {
        System.err.println("错误: " + error.getMessage());
    },
    () -> {
        System.out.println("\n[完成]");
    }
);
```

### Spring 集成

```java
@Configuration
public class QoderConfig {
    
    @Bean
    public QoderChatModel qoderChatModel(StreamingShellExample shellExample) {
        return QoderChatModel.builder()
            .executorService(shellExample.getReaderThreadPool())
            .activeProcesses(shellExample.getActiveProcesses())
            .workspace(Paths.get(System.getProperty("user.dir")))
            .timeoutSeconds(300)
            .build();
    }
}
```

## 🎯 命令格式

QoderChatModel 使用固定的命令模板:

```bash
qodercli -p "<用户问题>" --max-turns 25
```

**参数说明**:
- `-p`: 指定用户问题
- `--max-turns 25`: 最大交互轮数为 25

## ⚡ 关键特性

### 1. 同步调用 (call)
- 阻塞等待命令执行完成
- 返回完整的响应内容
- 适用于需要完整结果的场景

### 2. 流式调用 (stream)
- **实时输出**: 每行命令输出立即发送
- **非阻塞**: 不阻塞调用线程
- **可取消**: 支持中途取消
- **适用场景**: 
  - 长时间运行的命令
  - 需要实时反馈的场景
  - UI 界面实时显示

## ⚙️ 配置参数

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| executorService | ExecutorService | 必填 | 线程池 |
| activeProcesses | Set<Process> | 自动创建 | 活跃进程追踪 |
| workspace | Path | 当前目录 | 工作目录 |
| timeoutSeconds | long | 300 | 超时时间(秒) |

## 🔐 线程安全

- ✅ **线程池**: 使用 `ExecutorService` 管理并发
- ✅ **进程追踪**: 使用 `ConcurrentHashMap.newKeySet()` 线程安全集合
- ✅ **输出收集**: 每个任务独立的 `StringBuilder`
- ✅ **资源清理**: `finally` 块确保清理

## 🛡️ 异常处理

| 异常类型 | 触发条件 | 处理方式 |
|---------|---------|---------|
| TimeoutException | 执行超时 | 取消任务,抛出 RuntimeException |
| InterruptedException | 线程中断 | 取消任务,恢复中断状态 |
| ExecutionException | 命令执行失败 | 包装为 RuntimeException |
| IllegalArgumentException | Prompt 为空 | 直接抛出 |

## 📦 文件结构

```
examples/shelluse/src/main/java/com/alibaba/cloud/ai/examples/shelluse/
├── ProcessTask.java              # 进程任务封装
├── StreamingShellExample.java    # 基础设施提供者
├── QoderChatModel.java           # ChatModel 实现
└── QoderChatModelExample.java    # 使用示例
```

## 🚀 优势特性

1. **标准接口**: 符合 Spring AI ChatModel 接口规范
2. **双模式支持**: 同时支持同步和流式调用
3. **进程隔离**: 每次调用独立进程,互不影响
4. **异步执行**: 基于线程池异步执行,不阻塞调用方
5. **超时控制**: 可配置超时时间,防止无限等待
6. **资源管理**: 自动追踪和清理进程资源
7. **流式输出**: 实时收集命令输出
8. **Builder 模式**: 灵活的配置方式
9. **可取消**: 流式调用支持中途取消

## 🔄 扩展可能

1. **✅ 流式响应**: 已实现 `stream(Prompt)` 方法支持流式返回
2. **上下文管理**: 支持多轮对话上下文
3. **自定义命令**: 支持动态配置命令模板
4. **输出解析**: 支持结构化输出解析
5. **错误重试**: 支持失败重试机制

## 📝 最佳实践

1. **复用线程池**: 在 Spring 环境中复用 `StreamingShellExample` 的线程池
2. **设置超时**: 根据实际需求设置合理的超时时间
3. **优雅关闭**: 应用关闭时确保清理所有资源
4. **异常处理**: 捕获并妥善处理各类异常
5. **日志记录**: 添加适当的日志便于排查问题
