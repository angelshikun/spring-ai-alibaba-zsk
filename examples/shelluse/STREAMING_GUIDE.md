# Shell 执行方式对比：阻塞式 vs 流式

## 📋 问题描述

当使用 `ShellSessionManager` 执行长时间运行的命令（如 `qodercli`）时，会遇到以下问题：

- ❌ **需要等待命令完全执行完毕**才能看到任何输出
- ❌ **无法实时获取进度信息**，用户体验差
- ❌ **超时时间难以设置**，太短可能中断命令，太长用户等待时间过长

---

## 🔍 两种执行方式对比

### **方式1：阻塞式执行（ShellSessionManager）**

#### 特点
- ✅ 支持会话保持（环境变量、工作目录等状态保留）
- ✅ 可以连续执行多个命令
- ✅ 自动处理退出码和错误
- ❌ **必须等待命令完全执行完毕才能获取结果**
- ❌ 长时间命令体验差

#### 使用场景
- 快速执行的命令（几秒内完成）
- 需要会话状态保持的场景
- 需要在同一会话中执行多个命令

#### 代码示例
```java
ShellSessionManager sessionManager = ShellSessionManager.builder()
    .workspaceRoot(Path.of(workspaceRoot))
    .build();

RunnableConfig config = RunnableConfig.builder().build();
sessionManager.initialize(config);

// ⏳ 这里会阻塞，直到命令完全执行完毕
CommandResult result = sessionManager.executeCommand("ls -la", config);
System.out.println(result.getOutput());  // 一次性输出所有结果
```

#### 执行流程
```
用户调用 executeCommand()
         ↓
   写入命令到 stdin
         ↓
   等待收集所有输出  ⏳⏳⏳ (阻塞中...)
         ↓
   检测到完成标记
         ↓
   返回完整结果
         ↓
   显示输出
```

---

### **方式2：流式执行（StreamingShellExample）**

#### 特点
- ✅ **实时获取输出，无需等待命令完成**
- ✅ 用户体验好，可以看到实时进度
- ✅ 适合长时间运行的命令
- ✅ 支持超时控制
- ❌ 每次执行都启动新进程，无会话保持
- ❌ 需要手动处理输出

#### 使用场景
- 长时间运行的命令（如 AI 生成、大文件处理等）
- 需要实时反馈的场景
- 单次命令执行，不需要会话状态

#### 代码示例
```java
StreamingShellExample streaming = new StreamingShellExample();

int exitCode = streaming.executeStreamingCommand(
    "qodercli -p '介绍 Java'",
    workspace,
    line -> {
        // 🚀 每产生一行输出就立即调用这个回调
        System.out.print(line);
        System.out.flush();
    }
);
```

#### 执行流程
```
用户调用 executeStreamingCommand()
         ↓
   启动新的 Shell 进程
         ↓
   启动输出读取线程
         ↓
   命令开始执行
         ↓
   产生输出行 → 立即回调 outputConsumer ✨
   产生输出行 → 立即回调 outputConsumer ✨
   产生输出行 → 立即回调 outputConsumer ✨
         ↓
   命令完成
         ↓
   返回退出码
```

---

## 🎯 实际效果对比

### **阻塞式执行**

```bash
开始执行命令...
⏳ 等待中... (30秒)
⏳ 等待中... (30秒)
⏳ 等待中... (30秒)

[90秒后一次性显示]
输出行1
输出行2
输出行3
...
输出行100
完成！
```

### **流式执行**

```bash
开始执行命令...
输出行1 ✨ (立即显示)
输出行2 ✨ (立即显示)
输出行3 ✨ (立即显示)
...
输出行100 ✨ (立即显示)
完成！
```

---

## 📝 使用建议

### 使用阻塞式执行的情况：
1. 命令执行时间 < 5秒
2. 需要在同一会话中执行多个命令
3. 需要保持环境变量、工作目录等状态

### 使用流式执行的情况：
1. 命令执行时间 > 5秒
2. **AI 生成类命令**（如 qodercli、chatgpt-cli 等）
3. 大文件处理、网络下载等长时间操作
4. 需要实时反馈进度的场景

---

## 💻 完整示例代码

### 示例1：流式执行 qodercli

```java
@Component
public class QoderExample {
    
    @Autowired
    private StreamingShellExample streamingShell;
    
    public void askQoder(String question) {
        Path workspace = Path.of(System.getProperty("java.io.tmpdir"));
        
        System.out.println("正在向 Qoder 提问: " + question);
        System.out.println("----------------------------------------\n");
        
        int exitCode = streamingShell.executeStreamingCommand(
            String.format("qodercli -p '%s'", question),
            workspace,
            line -> {
                // 实时显示每一行输出
                System.out.print(line);
                System.out.flush();
            }
        );
        
        System.out.println("\n\n✅ 回答完成，退出码: " + exitCode);
    }
}
```

### 示例2：流式执行带进度显示

```java
public void downloadWithProgress(String url, String output) {
    AtomicInteger lineCount = new AtomicInteger(0);
    
    streamingShell.executeStreamingCommand(
        String.format("wget '%s' -O '%s'", url, output),
        workspace,
        line -> {
            int count = lineCount.incrementAndGet();
            System.out.printf("[%d] %s\n", count, line);
        }
    );
}
```

### 示例3：流式执行带超时

```java
public void longRunningCommandWithTimeout() {
    StreamingResult result = streamingShell.executeStreamingCommandWithTimeout(
        "npm install",  // 可能很慢的命令
        workspace,
        line -> System.out.println("[NPM] " + line),
        60000  // 60秒超时
    );
    
    if (result.isTimedOut()) {
        System.err.println("❌ 命令执行超时！");
    } else {
        System.out.println("✅ 命令执行成功");
    }
}
```

---

## 🔧 扩展：如何改造 ShellSessionManager 支持流式输出

如果需要在保持会话的同时支持流式输出，可以这样改造：

```java
// 在 ShellSessionManager 中添加流式执行方法
public void executeCommandStreaming(
    String command, 
    RunnableConfig config,
    Consumer<String> outputConsumer) {
    
    ShellSession session = (ShellSession) config.context()
        .get(SESSION_INSTANCE_CONTEXT_KEY);
    
    String marker = DONE_MARKER_PREFIX + UUID.randomUUID().toString();
    
    // 写入命令
    stdin.write(command + "\n");
    stdin.write(String.format("printf '%s %%s\\n' $?\n", marker));
    stdin.flush();
    
    // 实时读取输出
    while (true) {
        OutputLine outputLine = outputQueue.poll(100, TimeUnit.MILLISECONDS);
        
        if (outputLine != null && outputLine.content != null) {
            if (outputLine.content.startsWith(marker)) {
                break;  // 命令完成
            }
            // 🚀 立即回调
            outputConsumer.accept(outputLine.content);
        }
    }
}
```

---

## 📊 性能对比

| 指标 | 阻塞式 | 流式 |
|------|--------|------|
| 首次输出延迟 | 命令完成后 | 立即（毫秒级）|
| 内存占用 | 累积所有输出 | 逐行处理，内存友好 |
| 用户体验 | ⭐⭐ | ⭐⭐⭐⭐⭐ |
| 会话保持 | ✅ | ❌ |
| 适用场景 | 快速命令 | 长时间命令 |

---

## 🎯 总结

- **对于 qodercli 这类 AI 生成命令**，强烈建议使用**流式执行**方式
- 流式执行让用户能实时看到生成进度，大幅提升体验
- 如果需要会话保持 + 流式输出，可以参考扩展方案改造 ShellSessionManager

---

## 🚀 快速开始

运行流式输出示例：

```bash
cd examples/shelluse
./run.sh
```

查看 `StreamingShellExample.java` 了解更多实现细节。
