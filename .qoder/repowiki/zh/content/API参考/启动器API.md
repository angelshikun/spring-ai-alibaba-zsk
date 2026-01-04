# 启动器API

<cite>
**本文档中引用的文件**  
- [A2aServerProperties.java](file://spring-boot-starters/spring-ai-alibaba-starter-a2a-nacos/src/main/java/com/alibaba/cloud/ai/a2a/autoconfigure/A2aServerProperties.java)
- [A2aClientAgentCardProperties.java](file://spring-boot-starters/spring-ai-alibaba-starter-a2a-nacos/src/main/java/com/alibaba/cloud/ai/a2a/autoconfigure/A2aClientAgentCardProperties.java)
- [NacosA2aProperties.java](file://spring-boot-starters/spring-ai-alibaba-starter-a2a-nacos/src/main/java/com/alibaba/cloud/ai/a2a/registry/nacos/properties/NacosA2aProperties.java)
- [GraphObservationProperties.java](file://spring-boot-starters/spring-ai-alibaba-starter-graph-observation/src/main/java/com/alibaba/cloud/ai/autoconfigure/graph/GraphObservationProperties.java)
- [NacosOptions.java](file://spring-boot-starters/spring-ai-alibaba-starter-config-nacos/src/main/java/com/alibaba/cloud/ai/agent/nacos/NacosOptions.java)
- [additional-spring-configuration-metadata.json](file://spring-boot-starters/spring-ai-alibaba-starter-a2a-nacos/src/main/resources/META-INF/additional-spring-configuration-metadata.json)
- [application.yml](file://examples/documentation/src/main/resources/application.yml)
</cite>

## 目录
1. [简介](#简介)
2. [A2A-Nacos启动器配置](#a2a-nacos启动器配置)
3. [Config-Nacos启动器配置](#config-nacos启动器配置)
4. [Graph-Observation启动器配置](#graph-observation启动器配置)
5. [完整配置示例](#完整配置示例)

## 简介
本文档提供了Spring Boot启动器的配置属性API参考，详细说明了通过@ConfigurationProperties暴露的配置类。文档按照启动器模块组织，包括A2A-Nacos、Config-Nacos和Graph-Observation模块。每个配置属性都提供了详细的说明，包括其用途、默认值、有效范围和示例，以及这些属性如何影响框架行为。

## A2A-Nacos启动器配置

A2A-Nacos启动器提供了智能体到Nacos的注册与发现功能，支持A2A（Agent-to-Agent）协议的配置。该模块包含服务器端和客户端配置，以及Nacos注册中心的连接配置。

### A2A服务器配置
A2A服务器配置用于定义本地智能体的服务端点和基本属性。

| 配置属性 | 类型 | 默认值 | 描述 |
|---------|------|-------|------|
| `spring.ai.alibaba.a2a.server.type` | String | JSONRPC | A2A服务器类型，可选值：`JSONRPC`、`GRPC`、`HTTP+JSON` |
| `spring.ai.alibaba.a2a.server.agent-card-url` | String | /.well-known/agent.json | 智能体卡片的well-known URL |
| `spring.ai.alibaba.a2a.server.message-url` | String | /a2a | A2A服务器请求的基本上下文路径URL |
| `spring.ai.alibaba.a2a.server.address` | String | ${server.address} | A2A服务器请求的地址，优先使用server.address配置 |
| `spring.ai.alibaba.a2a.server.port` | Integer | ${server.port} | A2A服务器请求的端口，优先使用server.port配置 |
| `spring.ai.alibaba.a2a.server.version` | String | 1.0.0 | A2A服务器的版本号 |

**Section sources**
- [A2aServerProperties.java](file://spring-boot-starters/spring-ai-alibaba-starter-a2a-nacos/src/main/java/com/alibaba/cloud/ai/a2a/autoconfigure/A2aServerProperties.java#L42-L54)

### A2A客户端智能体卡片配置
A2A客户端智能体卡片配置用于定义智能体的基本信息和能力。

| 配置属性 | 类型 | 默认值 | 描述 |
|---------|------|-------|------|
| `spring.ai.alibaba.a2a.client.card.name` | String | - | 智能体的名称 |
| `spring.ai.alibaba.a2a.client.card.description` | String | - | 智能体的描述 |
| `spring.ai.alibaba.a2a.client.card.url` | String | - | 智能体的URL |
| `spring.ai.alibaba.a2a.client.card.provider` | AgentProvider | - | 智能体的提供者信息 |
| `spring.ai.alibaba.a2a.client.card.documentation-url` | String | - | 智能体的文档URL |
| `spring.ai.alibaba.a2a.client.card.capabilities` | AgentCapabilities | - | 智能体的能力 |
| `spring.ai.alibaba.a2a.client.card.default-input-modes` | List<String> | - | 智能体的默认输入模式 |
| `spring.ai.alibaba.a2a.client.card.default-output-modes` | List<String> | - | 智能体的默认输出模式 |
| `spring.ai.alibaba.a2a.client.card.skills` | List<AgentSkill> | - | 智能体的技能 |
| `spring.ai.alibaba.a2a.client.card.supports-authenticated-extended-card` | Boolean | false | 智能体是否支持认证的扩展卡片 |
| `spring.ai.alibaba.a2a.client.card.security-schemes` | Map<String, SecurityScheme> | - | 智能体的安全方案 |
| `spring.ai.alibaba.a2a.client.card.security` | List<Map<String, List<String>>> | - | 智能体的安全配置 |
| `spring.ai.alibaba.a2a.client.card.icon-url` | String | - | 智能体的图标URL |
| `spring.ai.alibaba.a2a.client.card.additional-interfaces` | List<AgentInterface> | - | 智能体的附加接口 |
| `spring.ai.alibaba.a2a.client.card.version` | String | - | 智能体的版本 |
| `spring.ai.alibaba.a2a.client.card.preferred-transport` | String | - | 智能体的首选传输方式，可选值：`JSONRPC`、`GRPC`、`HTTP+JSON` |
| `spring.ai.alibaba.a2a.client.card.protocol-version` | String | - | 智能体的协议版本 |
| `spring.ai.alibaba.a2a.client.card.well-known-url` | String | - | 智能体卡片的well-known URL |

**Section sources**
- [A2aClientAgentCardProperties.java](file://spring-boot-starters/spring-ai-alibaba-starter-a2a-nacos/src/main/java/com/alibaba/cloud/ai/a2a/autoconfigure/A2aClientAgentCardProperties.java#L29-L37)
- [additional-spring-configuration-metadata.json](file://spring-boot-starters/spring-ai-alibaba-starter-a2a-nacos/src/main/resources/META-INF/additional-spring-configuration-metadata.json#L36-L143)

### A2A服务器智能体卡片配置
A2A服务器智能体卡片配置继承自基础智能体卡片配置，用于定义服务器端智能体的特定属性。

| 配置属性 | 类型 | 默认值 | 描述 |
|---------|------|-------|------|
| `spring.ai.alibaba.a2a.server.card.name` | String | - | 智能体的名称 |
| `spring.ai.alibaba.a2a.server.card.description` | String | - | 智能体的描述 |
| `spring.ai.alibaba.a2a.server.card.url` | String | - | 智能体的URL |
| `spring.ai.alibaba.a2a.server.card.provider` | AgentProvider | - | 智能体的提供者信息 |
| `spring.ai.alibaba.a2a.server.card.documentation-url` | String | - | 智能体的文档URL |
| `spring.ai.alibaba.a2a.server.card.capabilities` | AgentCapabilities | - | 智能体的能力 |
| `spring.ai.alibaba.a2a.server.card.default-input-modes` | List<String> | - | 智能体的默认输入模式 |
| `spring.ai.alibaba.a2a.server.card.default-output-modes` | List<String> | - | 智能体的默认输出模式 |
| `spring.ai.alibaba.a2a.server.card.skills` | List<AgentSkill> | - | 智能体的技能 |
| `spring.ai.alibaba.a2a.server.card.supports-authenticated-extended-card` | Boolean | false | 智能体是否支持认证的扩展卡片 |
| `spring.ai.alibaba.a2a.server.card.security-schemes` | Map<String, SecurityScheme> | - | 智能体的安全方案 |
| `spring.ai.alibaba.a2a.server.card.security` | List<Map<String, List<String>>> | - | 智能体的安全配置 |
| `spring.ai.alibaba.a2a.server.card.icon-url` | String | - | 智能体的图标URL |
| `spring.ai.alibaba.a2a.server.card.additional-interfaces` | List<AgentInterface> | - | 智能体的附加接口 |

**Section sources**
- [A2aServerAgentCardProperties.java](file://spring-boot-starters/spring-ai-alibaba-starter-a2a-nacos/src/main/java/com/alibaba/cloud/ai/a2a/autoconfigure/A2aServerAgentCardProperties.java#L34)
- [additional-spring-configuration-metadata.json](file://spring-boot-starters/spring-ai-alibaba-starter-a2a-nacos/src/main/resources/META-INF/additional-spring-configuration-metadata.json#L255-L338)

### Nacos A2A注册配置
Nacos A2A注册配置用于定义与Nacos服务器的连接参数。

| 配置属性 | 类型 | 默认值 | 描述 |
|---------|------|-------|------|
| `spring.ai.alibaba.a2a.nacos.server-addr` | String | localhost:8848 | Nacos服务器地址 |
| `spring.ai.alibaba.a2a.nacos.namespace` | String | public | Nacos A2A服务的命名空间 |
| `spring.ai.alibaba.a2a.nacos.username` | String | "" | 用于认证的Nacos用户名 |
| `spring.ai.alibaba.a2a.nacos.password` | String | "" | 用于认证的Nacos密码 |
| `spring.ai.alibaba.a2a.nacos.access-key` | String | "" | 用于认证的Nacos访问密钥 |
| `spring.ai.alibaba.a2a.nacos.secret-key` | String | "" | 用于认证的Nacos密钥 |
| `spring.ai.alibaba.a2a.nacos.endpoint` | String | "" | Nacos服务器端点 |

**Section sources**
- [NacosA2aProperties.java](file://spring-boot-starters/spring-ai-alibaba-starter-a2a-nacos/src/main/java/com/alibaba/cloud/ai/a2a/registry/nacos/properties/NacosA2aProperties.java#L48-L67)
- [additional-spring-configuration-metadata.json](file://spring-boot-starters/spring-ai-alibaba-starter-a2a-nacos/src/main/resources/META-INF/additional-spring-configuration-metadata.json#L145-L192)

### Nacos A2A注册与发现配置
Nacos A2A注册与发现配置用于控制智能体在Nacos中的注册和发现行为。

| 配置属性 | 类型 | 默认值 | 描述 |
|---------|------|-------|------|
| `spring.ai.alibaba.a2a.nacos.registry.enabled` | Boolean | true | 是否启用Nacos自动将智能体卡片注册到Nacos |
| `spring.ai.alibaba.a2a.nacos.registry.register-as-latest` | Boolean | true | 是否将智能体卡片注册为最新版本 |
| `spring.ai.alibaba.a2a.nacos.discovery.enabled` | Boolean | true | 是否启用Nacos自动从Nacos发现智能体卡片 |

**Section sources**
- [additional-spring-configuration-metadata.json](file://spring-boot-starters/spring-ai-alibaba-starter-a2a-nacos/src/main/resources/META-INF/additional-spring-configuration-metadata.json#L194-L211)

## Config-Nacos启动器配置

Config-Nacos启动器提供了基于Nacos的配置管理功能，支持智能体配置的动态加载和加密。

### Nacos选项配置
Nacos选项配置类定义了与Nacos配置服务交互的各种参数和选项。

| 配置属性 | 类型 | 默认值 | 描述 |
|---------|------|-------|------|
| `encrypted` | boolean | false | 是否启用加密 |
| `modelEncrypted` | boolean | 与encrypted相同 | 模型配置是否加密 |
| `agentBaseEncrypted` | boolean | 与encrypted相同 | 智能体基础配置是否加密 |
| `promptEncrypted` | boolean | 与encrypted相同 | 提示词配置是否加密 |
| `mcpServersEncrypted` | boolean | 与encrypted相同 | MCP服务器配置是否加密 |
| `promptKey` | String | - | 提示词密钥 |
| `agentName` | String | - | 智能体名称 |
| `mcpNamespace` | String | 与namespace相同 | MCP命名空间 |

**Section sources**
- [NacosOptions.java](file://spring-boot-starters/spring-ai-alibaba-starter-config-nacos/src/main/java/com/alibaba/cloud/ai/agent/nacos/NacosOptions.java#L30-L53)

## Graph-Observation启动器配置

Graph-Observation启动器提供了图结构智能体的观测功能，支持Micrometer观测框架。

### 图观测配置
图观测配置用于控制图结构智能体的观测功能。

| 配置属性 | 类型 | 默认值 | 描述 |
|---------|------|-------|------|
| `spring.ai.alibaba.graph.observation.enabled` | boolean | true | 是否启用图观测功能 |

**Section sources**
- [GraphObservationProperties.java](file://spring-boot-starters/spring-ai-alibaba-starter-graph-observation/src/main/java/com/alibaba/cloud/ai/autoconfigure/graph/GraphObservationProperties.java#L29-L34)

## 完整配置示例

以下是一个完整的`application.yml`配置示例，展示了如何在实际应用中使用这些配置属性：

```yaml
server:
  port: 8080

spring:
  application:
    name: documentation-examples
  ai:
    # DashScope 配置（阿里云百炼）
    dashscope:
      api-key: ${DASHSCOPE_API_KEY:your-api-key}
      chat:
        options:
          model: ${DASHSCOPE_MODEL:qwen-plus}
    # A2A Nacos 配置
    alibaba:
      a2a:
        nacos:
          server-addr: ${NACOS_SERVER_ADDR:127.0.0.1:8848}
          username: ${NACOS_USERNAME:nacos}
          password: ${NACOS_PASSWORD:nacos}
          discovery:
            enabled: true  # 启用 A2A 服务发现，用于发现远程智能体
          registry:
            enabled: true  # 启用 Nacos Registry，将本地 Agent 注册到 Nacos
        server:
          version: 1.0.0
          card:
            name: data_analysis_agent
            description: 专门用于数据分析和统计计算的本地智能体
            provider:
              name: Spring AI Alibaba Documentation
              organization: Spring AI Alibaba
              url: https://sca.aliyun.com/ai/
              contact:
                email: dev@alibabacloud.com

# 日志配置
logging:
  level:
    root: INFO
    com.alibaba.cloud.ai: DEBUG
    org.springframework.ai: DEBUG
```

**Section sources**
- [application.yml](file://examples/documentation/src/main/resources/application.yml#L1-L43)