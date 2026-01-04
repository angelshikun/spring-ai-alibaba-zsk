# API参考

<cite>
**本文档引用的文件**   
- [Agent.java](file://spring-ai-alibaba-agent-framework/src/main/java/com/alibaba/cloud/ai/graph/agent/Agent.java)
- [ReactAgent.java](file://spring-ai-alibaba-agent-framework/src/main/java/com/alibaba/cloud/ai/graph/agent/ReactAgent.java)
- [GraphRunner.java](file://spring-ai-alibaba-graph-core/src/main/java/com/alibaba/cloud/ai/graph/GraphRunner.java)
- [RunnableConfig.java](file://spring-ai-alibaba-graph-core/src/main/java/com/alibaba/cloud/ai/graph/RunnableConfig.java)
- [GraphResponse.java](file://spring-ai-alibaba-graph-core/src/main/java/com/alibaba/cloud/ai/graph/GraphResponse.java)
- [CompiledGraph.java](file://spring-ai-alibaba-graph-core/src/main/java/com/alibaba/cloud/ai/graph/CompiledGraph.java)
- [NodeOutput.java](file://spring-ai-alibaba-graph-core/src/main/java/com/alibaba/cloud/ai/graph/NodeOutput.java)
- [OverAllState.java](file://spring-ai-alibaba-graph-core/src/main/java/com/alibaba/cloud/ai/graph/OverAllState.java)
</cite>

## 目录
1. [代理框架API](#代理框架api)
2. [图核心API](#图核心api)
3. [公共类和接口](#公共类和接口)

## 代理框架API

### Agent接口

`Agent` 是所有代理的抽象基类，定义了代理的核心属性和方法。

**Section sources**
- [Agent.java](file://spring-ai-alibaba-agent-framework/src/main/java/com/alibaba/cloud/ai/graph/agent/Agent.java#L48-L329)

#### run()方法

`run()` 方法是代理执行的核心入口，提供了多种重载形式来处理不同的输入类型和配置。

```java
public Optional<OverAllState> invoke(String message) throws GraphRunnerException
```
- **参数**:
  - `message` (String): 输入消息字符串。
- **返回值**: `Optional<OverAllState>` - 包含执行结果的可选状态对象。
- **异常**: `GraphRunnerException` - 当执行过程中发生错误时抛出。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public Optional<OverAllState> invoke(String message, RunnableConfig config) throws GraphRunnerException
```
- **参数**:
  - `message` (String): 输入消息字符串。
  - `config` (RunnableConfig): 运行配置对象。
- **返回值**: `Optional<OverAllState>` - 包含执行结果的可选状态对象。
- **异常**: `GraphRunnerException` - 当执行过程中发生错误时抛出。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public Optional<OverAllState> invoke(UserMessage message) throws GraphRunnerException
```
- **参数**:
  - `message` (UserMessage): 用户消息对象。
- **返回值**: `Optional<OverAllState>` - 包含执行结果的可选状态对象。
- **异常**: `GraphRunnerException` - 当执行过程中发生错误时抛出。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public Optional<OverAllState> invoke(UserMessage message, RunnableConfig config) throws GraphRunnerException
```
- **参数**:
  - `message` (UserMessage): 用户消息对象。
  - `config` (RunnableConfig): 运行配置对象。
- **返回值**: `Optional<OverAllState>` - 包含执行结果的可选状态对象。
- **异常**: `GraphRunnerException` - 当执行过程中发生错误时抛出。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public Optional<OverAllState> invoke(List<Message> messages) throws GraphRunnerException
```
- **参数**:
  - `messages` (List<Message>): 消息列表。
- **返回值**: `Optional<OverAllState>` - 包含执行结果的可选状态对象。
- **异常**: `GraphRunnerException` - 当执行过程中发生错误时抛出。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public Optional<OverAllState> invoke(List<Message> messages, RunnableConfig config) throws GraphRunnerException
```
- **参数**:
  - `messages` (List<Message>): 消息列表。
  - `config` (RunnableConfig): 运行配置对象。
- **返回值**: `Optional<OverAllState>` - 包含执行结果的可选状态对象。
- **异常**: `GraphRunnerException` - 当执行过程中发生错误时抛出。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

#### invokeAndGetOutput()方法

`invokeAndGetOutput()` 方法返回包含节点输出的可选对象。

```java
public Optional<NodeOutput> invokeAndGetOutput(String message) throws GraphRunnerException
```
- **参数**:
  - `message` (String): 输入消息字符串。
- **返回值**: `Optional<NodeOutput>` - 包含节点输出的可选对象。
- **异常**: `GraphRunnerException` - 当执行过程中发生错误时抛出。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public Optional<NodeOutput> invokeAndGetOutput(String message, RunnableConfig config) throws GraphRunnerException
```
- **参数**:
  - `message` (String): 输入消息字符串。
  - `config` (RunnableConfig): 运行配置对象。
- **返回值**: `Optional<NodeOutput>` - 包含节点输出的可选对象。
- **异常**: `GraphRunnerException` - 当执行过程中发生错误时抛出。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public Optional<NodeOutput> invokeAndGetOutput(UserMessage message) throws GraphRunnerException
```
- **参数**:
  - `message` (UserMessage): 用户消息对象。
- **返回值**: `Optional<NodeOutput>` - 包含节点输出的可选对象。
- **异常**: `GraphRunnerException` - 当执行过程中发生错误时抛出。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public Optional<NodeOutput> invokeAndGetOutput(UserMessage message, RunnableConfig config) throws GraphRunnerException
```
- **参数**:
  - `message` (UserMessage): 用户消息对象。
  - `config` (RunnableConfig): 运行配置对象。
- **返回值**: `Optional<NodeOutput>` - 包含节点输出的可选对象。
- **异常**: `GraphRunnerException` - 当执行过程中发生错误时抛出。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public Optional<NodeOutput> invokeAndGetOutput(List<Message> messages) throws GraphRunnerException
```
- **参数**:
  - `messages` (List<Message>): 消息列表。
- **返回值**: `Optional<NodeOutput>` - 包含节点输出的可选对象。
- **异常**: `GraphRunnerException` - 当执行过程中发生错误时抛出。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public Optional<NodeOutput> invokeAndGetOutput(List<Message> messages, RunnableConfig config) throws GraphRunnerException
```
- **参数**:
  - `messages` (List<Message>): 消息列表。
  - `config` (RunnableConfig): 运行配置对象。
- **返回值**: `Optional<NodeOutput>` - 包含节点输出的可选对象。
- **异常**: `GraphRunnerException` - 当执行过程中发生错误时抛出。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

#### stream()方法

`stream()` 方法提供流式输出，允许实时处理代理的输出。

```java
public Flux<NodeOutput> stream(String message) throws GraphRunnerException
```
- **参数**:
  - `message` (String): 输入消息字符串。
- **返回值**: `Flux<NodeOutput>` - 包含节点输出的反应式流。
- **异常**: `GraphRunnerException` - 当执行过程中发生错误时抛出。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public Flux<NodeOutput> stream(String message, RunnableConfig config) throws GraphRunnerException
```
- **参数**:
  - `message` (String): 输入消息字符串。
  - `config` (RunnableConfig): 运行配置对象。
- **返回值**: `Flux<NodeOutput>` - 包含节点输出的反应式流。
- **异常**: `GraphRunnerException` - 当执行过程中发生错误时抛出。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public Flux<NodeOutput> stream(UserMessage message) throws GraphRunnerException
```
- **参数**:
  - `message` (UserMessage): 用户消息对象。
- **返回值**: `Flux<NodeOutput>` - 包含节点输出的反应式流。
- **异常**: `GraphRunnerException` - 当执行过程中发生错误时抛出。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public Flux<NodeOutput> stream(UserMessage message, RunnableConfig config) throws GraphRunnerException
```
- **参数**:
  - `message` (UserMessage): 用户消息对象。
  - `config` (RunnableConfig): 运行配置对象。
- **返回值**: `Flux<NodeOutput>` - 包含节点输出的反应式流。
- **异常**: `GraphRunnerException` - 当执行过程中发生错误时抛出。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public Flux<NodeOutput> stream(List<Message> messages) throws GraphRunnerException
```
- **参数**:
  - `messages` (List<Message>): 消息列表。
- **返回值**: `Flux<NodeOutput>` - 包含节点输出的反应式流。
- **异常**: `GraphRunnerException` - 当执行过程中发生错误时抛出。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public Flux<NodeOutput> stream(List<Message> messages, RunnableConfig config) throws GraphRunnerException
```
- **参数**:
  - `messages` (List<Message>): 消息列表。
  - `config` (RunnableConfig): 运行配置对象。
- **返回值**: `Flux<NodeOutput>` - 包含节点输出的反应式流。
- **异常**: `GraphRunnerException` - 当执行过程中发生错误时抛出。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

### ReactAgent类

`ReactAgent` 是一个具体的代理实现，支持ReAct模式。

**Section sources**
- [ReactAgent.java](file://spring-ai-alibaba-agent-framework/src/main/java/com/alibaba/cloud/ai/graph/agent/ReactAgent.java#L93-L1007)

#### call()方法

`call()` 方法是 `ReactAgent` 的主要调用方法，返回 `AssistantMessage` 对象。

```java
public AssistantMessage call(String message) throws GraphRunnerException
```
- **参数**:
  - `message` (String): 输入消息字符串。
- **返回值**: `AssistantMessage` - 助手消息对象。
- **异常**: `GraphRunnerException` - 当执行过程中发生错误时抛出。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public AssistantMessage call(String message, RunnableConfig config) throws GraphRunnerException
```
- **参数**:
  - `message` (String): 输入消息字符串。
  - `config` (RunnableConfig): 运行配置对象。
- **返回值**: `AssistantMessage` - 助手消息对象。
- **异常**: `GraphRunnerException` - 当执行过程中发生错误时抛出。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public AssistantMessage call(UserMessage message) throws GraphRunnerException
```
- **参数**:
  - `message` (UserMessage): 用户消息对象。
- **返回值**: `AssistantMessage` - 助手消息对象。
- **异常**: `GraphRunnerException` - 当执行过程中发生错误时抛出。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public AssistantMessage call(UserMessage message, RunnableConfig config) throws GraphRunnerException
```
- **参数**:
  - `message` (UserMessage): 用户消息对象。
  - `config` (RunnableConfig): 运行配置对象。
- **返回值**: `AssistantMessage` - 助手消息对象。
- **异常**: `GraphRunnerException` - 当执行过程中发生错误时抛出。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public AssistantMessage call(List<Message> messages) throws GraphRunnerException
```
- **参数**:
  - `messages` (List<Message>): 消息列表。
- **返回值**: `AssistantMessage` - 助手消息对象。
- **异常**: `GraphRunnerException` - 当执行过程中发生错误时抛出。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public AssistantMessage call(List<Message> messages, RunnableConfig config) throws GraphRunnerException
```
- **参数**:
  - `messages` (List<Message>): 消息列表。
  - `config` (RunnableConfig): 运行配置对象。
- **返回值**: `AssistantMessage` - 助手消息对象。
- **异常**: `GraphRunnerException` - 当执行过程中发生错误时抛出。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

## 图核心API

### GraphRunner类

`GraphRunner` 是图执行引擎，负责执行编译后的图。

**Section sources**
- [GraphRunner.java](file://spring-ai-alibaba-graph-core/src/main/java/com/alibaba/cloud/ai/graph/GraphRunner.java#L24-L64)

#### execute()方法

`execute()` 方法执行图并返回反应式流。

```java
public Flux<GraphResponse<NodeOutput>> run(OverAllState initialState)
```
- **参数**:
  - `initialState` (OverAllState): 初始状态对象。
- **返回值**: `Flux<GraphResponse<NodeOutput>>` - 包含图响应的反应式流。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

### RunnableConfig类

`RunnableConfig` 类表示可运行任务的配置。

**Section sources**
- [RunnableConfig.java](file://spring-ai-alibaba-graph-core/src/main/java/com/alibaba/cloud/ai/graph/RunnableConfig.java#L32-L398)

#### Builder类

`Builder` 类用于构建 `RunnableConfig` 对象。

```java
public static Builder builder()
```
- **返回值**: `Builder` - 新的构建器实例。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public static Builder builder(RunnableConfig config)
```
- **参数**:
  - `config` (RunnableConfig): 现有的配置对象。
- **返回值**: `Builder` - 基于现有配置的新构建器实例。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

#### threadId()方法

```java
public Builder threadId(String threadId)
```
- **参数**:
  - `threadId` (String): 线程ID。
- **返回值**: `Builder` - 构建器实例，支持方法链式调用。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

#### checkPointId()方法

```java
public Builder checkPointId(String checkPointId)
```
- **参数**:
  - `checkPointId` (String): 检查点ID。
- **返回值**: `Builder` - 构建器实例，支持方法链式调用。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

#### nextNode()方法

```java
public Builder nextNode(String nextNode)
```
- **参数**:
  - `nextNode` (String): 下一个节点ID。
- **返回值**: `Builder` - 构建器实例，支持方法链式调用。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

#### streamMode()方法

```java
public Builder streamMode(CompiledGraph.StreamMode streamMode)
```
- **参数**:
  - `streamMode` (CompiledGraph.StreamMode): 流模式。
- **返回值**: `Builder` - 构建器实例，支持方法链式调用。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

#### addHumanFeedback()方法

```java
public Builder addHumanFeedback(InterruptionMetadata humanFeedback)
```
- **参数**:
  - `humanFeedback` (InterruptionMetadata): 人类反馈元数据。
- **返回值**: `Builder` - 构建器实例，支持方法链式调用。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

#### addStateUpdate()方法

```java
public Builder addStateUpdate(Map<String, Object> stateUpdate)
```
- **参数**:
  - `stateUpdate` (Map<String, Object>): 状态更新。
- **返回值**: `Builder` - 构建器实例，支持方法链式调用。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

#### addParallelNodeExecutor()方法

```java
public Builder addParallelNodeExecutor(String nodeId, Executor executor)
```
- **参数**:
  - `nodeId` (String): 并行节点ID。
  - `executor` (Executor): 执行器。
- **返回值**: `Builder` - 构建器实例，支持方法链式调用。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

#### defaultParallelExecutor()方法

```java
public Builder defaultParallelExecutor(Executor executor)
```
- **参数**:
  - `executor` (Executor): 默认并行执行器。
- **返回值**: `Builder` - 构建器实例，支持方法链式调用。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

#### clearContext()方法

```java
public Builder clearContext()
```
- **返回值**: `Builder` - 构建器实例，支持方法链式调用。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

#### store()方法

```java
public Builder store(Store store)
```
- **参数**:
  - `store` (Store): 存储对象。
- **返回值**: `Builder` - 构建器实例，支持方法链式调用。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

#### build()方法

```java
public RunnableConfig build()
```
- **返回值**: `RunnableConfig` - 构建的配置对象。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

## 公共类和接口

### GraphResponse类

`GraphResponse` 类表示图元素在Flux中的响应。

**Section sources**
- [GraphResponse.java](file://spring-ai-alibaba-graph-core/src/main/java/com/alibaba/cloud/ai/graph/GraphResponse.java#L28-L176)

#### 构造函数

```java
public GraphResponse()
```
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public GraphResponse(CompletableFuture<E> data, Object resultValue)
```
- **参数**:
  - `data` (CompletableFuture<E>): 数据。
  - `resultValue` (Object): 结果值。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public GraphResponse(CompletableFuture<E> data, Object resultValue, Map<String, Object> metadata)
```
- **参数**:
  - `data` (CompletableFuture<E>): 数据。
  - `resultValue` (Object): 结果值。
  - `metadata` (Map<String, Object>): 元数据。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

#### 静态工厂方法

```java
public static <E> GraphResponse<E> of(CompletableFuture<E> data)
```
- **参数**:
  - `data` (CompletableFuture<E>): 数据。
- **返回值**: `GraphResponse<E>` - 图响应对象。
- **稳定性**: 稳定
- **线程安全性**: 稳定

```java
public static <E> GraphResponse<E> of(CompletableFuture<E> data, Map<String, Object> metadata)
```
- **参数**:
  - `data` (CompletableFuture<E>): 数据。
  - `metadata` (Map<String, Object>): 元数据。
- **返回值**: `GraphResponse<E>` - 图响应对象。
- **稳定性**: 稳定
- **线程安全性**: 稳定

```java
public static <E> GraphResponse<E> of(E data)
```
- **参数**:
  - `data` (E): 数据。
- **返回值**: `GraphResponse<E>` - 图响应对象。
- **稳定性**: 稳定
- **线程安全性**: 稳定

```java
public static <E> GraphResponse<E> of(E data, Map<String, Object> metadata)
```
- **参数**:
  - `data` (E): 数据。
  - `metadata` (Map<String, Object>): 元数据。
- **返回值**: `GraphResponse<E>` - 图响应对象。
- **稳定性**: 稳定
- **线程安全性**: 稳定

```java
public static <E> GraphResponse<E> done()
```
- **返回值**: `GraphResponse<E>` - 完成的图响应对象。
- **稳定性**: 稳定
- **线程安全性**: 稳定

```java
public static <E> GraphResponse<E> done(Object resultValue)
```
- **参数**:
  - `resultValue` (Object): 结果值。
- **返回值**: `GraphResponse<E>` - 完成的图响应对象。
- **稳定性**: 稳定
- **线程安全性**: 稳定

```java
public static <E> GraphResponse<E> done(Object resultValue, Map<String, Object> metadata)
```
- **参数**:
  - `resultValue` (Object): 结果值。
  - `metadata` (Map<String, Object>): 元数据。
- **返回值**: `GraphResponse<E>` - 完成的图响应对象。
- **稳定性**: 稳定
- **线程安全性**: 稳定

```java
public static <E> GraphResponse<E> error(Throwable exception)
```
- **参数**:
  - `exception` (Throwable): 异常。
- **返回值**: `GraphResponse<E>` - 错误的图响应对象。
- **稳定性**: 稳定
- **线程安全性**: 稳定

```java
public static <E> GraphResponse<E> error(Throwable exception, Map<String, Object> metadata)
```
- **参数**:
  - `exception` (Throwable): 异常。
  - `metadata` (Map<String, Object>): 元数据。
- **返回值**: `GraphResponse<E>` - 错误的图响应对象。
- **稳定性**: 稳定
- **线程安全性**: 稳定

#### 实例方法

```java
public CompletableFuture<E> getOutput()
```
- **返回值**: `CompletableFuture<E>` - 输出。
- **稳定性**: 稳定
- **线程安全性**: 稳定

```java
public Optional<Object> resultValue()
```
- **返回值**: `Optional<Object>` - 结果值。
- **稳定性**: 稳定
- **线程安全性**: 稳定

```java
public boolean isDone()
```
- **返回值**: `boolean` - 是否完成。
- **稳定性**: 稳定
- **线程安全性**: 稳定

```java
public boolean isError()
```
- **返回值**: `boolean` - 是否错误。
- **稳定性**: 稳定
- **线程安全性**: 稳定

```java
public void addMetadata(String key, Object value)
```
- **参数**:
  - `key` (String): 元数据键。
  - `value` (Object): 元数据值。
- **稳定性**: 稳定
- **线程安全性**: 稳定

```java
public Object getMetadata(String key)
```
- **参数**:
  - `key` (String): 元数据键。
- **返回值**: `Object` - 元数据值。
- **稳定性**: 稳定
- **线程安全性**: 稳定

```java
public <T> T getMetadata(String key, Class<T> type)
```
- **参数**:
  - `key` (String): 元数据键。
  - `type` (Class<T>): 期望类型。
- **返回值**: `T` - 元数据值。
- **稳定性**: 稳定
- **线程安全性**: 稳定

```java
public Map<String, Object> getAllMetadata()
```
- **返回值**: `Map<String, Object>` - 所有元数据。
- **稳定性**: 稳定
- **线程安全性**: 稳定

```java
public boolean hasMetadata(String key)
```
- **参数**:
  - `key` (String): 元数据键。
- **返回值**: `boolean` - 是否存在元数据。
- **稳定性**: 稳定
- **线程安全性**: 稳定

```java
public Object removeMetadata(String key)
```
- **参数**:
  - `key` (String): 元数据键。
- **返回值**: `Object` - 之前的值。
- **稳定性**: 稳定
- **线程安全性**: 稳定

### CompiledGraph类

`CompiledGraph` 类表示编译后的图。

**Section sources**
- [CompiledGraph.java](file://spring-ai-alibaba-graph-core/src/main/java/com/alibaba/cloud/ai/graph/CompiledGraph.java#L61-L807)

#### invoke()方法

```java
public Optional<OverAllState> invoke(Map<String, Object> inputs, RunnableConfig config)
```
- **参数**:
  - `inputs` (Map<String, Object>): 输入。
  - `config` (RunnableConfig): 配置。
- **返回值**: `Optional<OverAllState>` - 最终状态。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public Optional<OverAllState> invoke(OverAllState overAllState, RunnableConfig config)
```
- **参数**:
  - `overAllState` (OverAllState): 初始状态。
  - `config` (RunnableConfig): 配置。
- **返回值**: `Optional<OverAllState>` - 最终状态。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public Optional<OverAllState> invoke(Map<String, Object> inputs)
```
- **参数**:
  - `inputs` (Map<String, Object>): 输入。
- **返回值**: `Optional<OverAllState>` - 最终状态。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

#### invokeAndGetOutput()方法

```java
public Optional<NodeOutput> invokeAndGetOutput(OverAllState overAllState, RunnableConfig config)
```
- **参数**:
  - `overAllState` (OverAllState): 初始状态。
  - `config` (RunnableConfig): 配置。
- **返回值**: `Optional<NodeOutput>` - 节点输出。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public Optional<NodeOutput> invokeAndGetOutput(Map<String, Object> inputs, RunnableConfig config)
```
- **参数**:
  - `inputs` (Map<String, Object>): 输入。
  - `config` (RunnableConfig): 配置。
- **返回值**: `Optional<NodeOutput>` - 节点输出。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public Optional<NodeOutput> invokeAndGetOutput(Map<String, Object> inputs)
```
- **参数**:
  - `inputs` (Map<String, Object>): 输入。
- **返回值**: `Optional<NodeOutput>` - 节点输出。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

#### stream()方法

```java
public Flux<NodeOutput> stream(Map<String, Object> inputs, RunnableConfig config)
```
- **参数**:
  - `inputs` (Map<String, Object>): 输入。
  - `config` (RunnableConfig): 配置。
- **返回值**: `Flux<NodeOutput>` - 节点输出流。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public Flux<NodeOutput> streamFromInitialNode(OverAllState overAllState, RunnableConfig config)
```
- **参数**:
  - `overAllState` (OverAllState): 初始状态。
  - `config` (RunnableConfig): 配置。
- **返回值**: `Flux<NodeOutput>` - 节点输出流。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public Flux<NodeOutput> stream(Map<String, Object> inputs)
```
- **参数**:
  - `inputs` (Map<String, Object>): 输入。
- **返回值**: `Flux<NodeOutput>` - 节点输出流。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public Flux<NodeOutput> stream()
```
- **返回值**: `Flux<NodeOutput>` - 节点输出流。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public Flux<NodeOutput> streamSnapshots(Map<String, Object> inputs, RunnableConfig config)
```
- **参数**:
  - `inputs` (Map<String, Object>): 输入。
  - `config` (RunnableConfig): 配置。
- **返回值**: `Flux<NodeOutput>` - 快照流。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

#### schedule()方法

```java
public ScheduledAgentTask schedule(ScheduleConfig scheduleConfig)
```
- **参数**:
  - `scheduleConfig` (ScheduleConfig): 调度配置。
- **返回值**: `ScheduledAgentTask` - 调度任务。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

### NodeOutput类

`NodeOutput` 类表示图中节点的输出。

**Section sources**
- [NodeOutput.java](file://spring-ai-alibaba-graph-core/src/main/java/com/alibaba/cloud/ai/graph/NodeOutput.java#L26-L125)

#### 静态工厂方法

```java
public static NodeOutput of(String node, String agentName, OverAllState state, Usage tokenUsage)
```
- **参数**:
  - `node` (String): 节点标识符。
  - `agentName` (String): 代理名称。
  - `state` (OverAllState): 状态。
  - `tokenUsage` (Usage): 令牌使用情况。
- **返回值**: `NodeOutput` - 节点输出对象。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

#### 实例方法

```java
public boolean isSTART()
```
- **返回值**: `boolean` - 是否为开始节点。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public boolean isEND()
```
- **返回值**: `boolean` - 是否为结束节点。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public boolean isSubGraph()
```
- **返回值**: `boolean` - 是否为子图。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public NodeOutput setSubGraph(boolean subGraph)
```
- **参数**:
  - `subGraph` (boolean): 是否为子图。
- **返回值**: `NodeOutput` - 节点输出对象。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public void setTokenUsage(Usage tokenUsage)
```
- **参数**:
  - `tokenUsage` (Usage): 令牌使用情况。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public String node()
```
- **返回值**: `String` - 节点标识符。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public String agent()
```
- **返回值**: `String` - 代理名称。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public Usage tokenUsage()
```
- **返回值**: `Usage` - 令牌使用情况。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public OverAllState state()
```
- **返回值**: `OverAllState` - 状态。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

### OverAllState类

`OverAllState` 类表示图或工作流执行的总体状态。

**Section sources**
- [OverAllState.java](file://spring-ai-alibaba-graph-core/src/main/java/com/alibaba/cloud/ai/graph/OverAllState.java#L77-L561)

#### 构造函数

```java
public OverAllState(Map<String, Object> data)
```
- **参数**:
  - `data` (Map<String, Object>): 数据。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public OverAllState(Map<String, Object> data, Store store)
```
- **参数**:
  - `data` (Map<String, Object>): 数据。
  - `store` (Store): 存储对象。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public OverAllState()
```
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public OverAllState(Store store)
```
- **参数**:
  - `store` (Store): 存储对象。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

#### 实例方法

```java
public void reset()
```
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public Optional<OverAllState> snapShot()
```
- **返回值**: `Optional<OverAllState>` - 状态快照。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public void clear()
```
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public void cover(OverAllState overAllState)
```
- **参数**:
  - `overAllState` (OverAllState): 状态对象。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public OverAllState input(Map<String, Object> input)
```
- **参数**:
  - `input` (Map<String, Object>): 输入。
- **返回值**: `OverAllState` - 状态对象。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public OverAllState registerKeyAndStrategy(String key, KeyStrategy strategy)
```
- **参数**:
  - `key` (String): 键。
  - `strategy` (KeyStrategy): 策略。
- **返回值**: `OverAllState` - 状态对象。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public OverAllState registerKeyAndStrategy(Map<String, KeyStrategy> keyStrategies)
```
- **参数**:
  - `keyStrategies` (Map<String, KeyStrategy>): 键策略映射。
- **返回值**: `OverAllState` - 状态对象。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public boolean containStrategy(String key)
```
- **参数**:
  - `key` (String): 键。
- **返回值**: `boolean` - 是否包含策略。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public Map<String, Object> updateState(Map<String, Object> partialState)
```
- **参数**:
  - `partialState` (Map<String, Object>): 部分状态。
- **返回值**: `Map<String, Object>` - 更新后的状态。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public void updateStateWithKeyStrategies(Map<String, Object> partialState, Map<String, KeyStrategy> keyStrategyMap)
```
- **参数**:
  - `partialState` (Map<String, Object>): 部分状态。
  - `keyStrategyMap` (Map<String, KeyStrategy>): 键策略映射。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public void updateStateBySchema(Map<String, Object> state, Map<String, Object> partialState, Map<String, KeyStrategy> keyStrategies)
```
- **参数**:
  - `state` (Map<String, Object>): 状态。
  - `partialState` (Map<String, Object>): 部分状态。
  - `keyStrategies` (Map<String, KeyStrategy>): 键策略映射。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public Map<String, KeyStrategy> keyStrategies()
```
- **返回值**: `Map<String, KeyStrategy>` - 键策略映射。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public final Map<String, Object> data()
```
- **返回值**: `Map<String, Object>` - 数据。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public final <T> Optional<T> value(String key)
```
- **参数**:
  - `key` (String): 键。
- **返回值**: `Optional<T>` - 值。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public final <T> Optional<T> value(String key, Class<T> type)
```
- **参数**:
  - `key` (String): 键。
  - `type` (Class<T>): 类型。
- **返回值**: `Optional<T>` - 值。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public final <T> T value(String key, T defaultValue)
```
- **参数**:
  - `key` (String): 键。
  - `defaultValue` (T): 默认值。
- **返回值**: `T` - 值。
- **稳定性**: 稳定
- **线程安全性**: 线程安全

```java
public Store getStore()
```
- **返回值**: `Store` - 存储对象。
- **稳定性**: 稳定
- **线程安全性**: 线程安全