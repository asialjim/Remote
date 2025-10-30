# Remote
#### 介绍
    Remote 是一个声明式的 Java 流程处理框架
    基于此框架，任何业务流程均可以以注解+生命周期的模式，将该流程拆分，并组装到一个Java接口当中，且仅需要关心该流程处理的各自步骤即可，而不用关心该接口的实现。
    Remote 设计了一套回调接口，将Java流程处理当中的每一步细分到具体的回调接口中。用户可根据业务流程处理的实际情况，充分解耦该流程的每一步，最后利用Remote将这些步骤有机结合起来完成用户想要的业务逻辑
    当前 Remote 主要服务于利用 Remote 构建基于 HTTP/HTTPS 协议的三方公众平台 API 的客户端
    利用此客户端，用户可以像写 Feign 接口一样来调用三方系统的 API 并完成自己的业务逻辑处理
    当然， 我们依然可以基于 remote-core 带来的特性进行三方扩展，比如利用 remote-core 写一个从excel 表格中获取数据并按照一定规则将数据进行计算并进行其他业务操作。

#### 软件架构
    remote-core： Remote 框架核心架构，此库定义了Remote的一等公民：RemoteLifeCycle、LifeCycle、CallBack 基于此三个接口/注解，用户可以扩展任何想要的框架。核心模块提供了丰富的回调接口实现，包括Before、After、Finally、Invoke、On、OnError、OnFail、OnRetry、OnSuccess、RetryWhen和SuccessWhen等，支持流程的完整生命周期管理。
    remote-net:   Remote 网络包，定义了与网络相关的部分库，利用 @Server 注解声明用户构建的客户端想要连接的网络系统（服务器），并提供Remote网络环境仓库接口，用户可利用该接口完成对三方网络系统环境的管理，特别适用于在内网环境下，调用三方公众平台 API 域名被代理的情况。
    remote-net-repository-mybatis-plus: 基于MyBatis-Plus的Remote网络环境仓库管理方案
    remote-net-repository-mybatis-flex: 基于MyBatis-Flex的Remote网络环境仓库管理方案
    remote-net-netty: 基于Netty的网络通信基础框架
    remote-net-http:   Remote 对HTTP/HTTPS协议下的API客户端适配，定义了HTTP相关的注解和生命周期处理
    remote-net-http-netty: 基于Netty的HTTP网络请求框架
    remote-net-http-apache: 基于Apache HttpClient的HTTP网络请求框架
    remote-net-proxy:   网络代理框架，提供代理支持功能
    remote-spring:    Remote与Spring的集成模块，方便在Spring项目中使用
    remote-zen-pom:    提供一个类似于spring-boot-dependencies的版本管理库，统一管理依赖版本

#### 安装教程
```xml
<!--引入版本管理-->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.asialjim.microapplet</groupId>
            <artifactId>remote-zen-pom</artifactId>
            <version>${remote.version}</version>
            <scope>import</scope>
            <type>pom</type>
        </dependency>
    </dependencies>
</dependencyManagement>

<!--按需要引入相关库，例如构建HTTP客户端引入-->
<dependencies>
    <!-- 核心依赖 -->
    <dependency>
        <groupId>com.asialjim.microapplet</groupId>
        <artifactId>remote-core</artifactId>
    </dependency>
    
    <!-- HTTP支持 -->
    <dependency>
        <groupId>com.asialjim.microapplet</groupId>
        <artifactId>remote-net-http</artifactId>
    </dependency>
    
    <!-- 选择一个HTTP实现 -->
    <dependency>
        <groupId>com.asialjim.microapplet</groupId>
        <artifactId>remote-net-http-netty</artifactId>
    </dependency>
    <!-- 或者 -->
    <!-- 
    <dependency>
        <groupId>com.asialjim.microapplet</groupId>
        <artifactId>remote-net-http-apache</artifactId>
    </dependency>
    -->
    
    <!-- Spring集成（可选） -->
    <dependency>
        <groupId>com.asialjim.microapplet</groupId>
        <artifactId>remote-spring</artifactId>
    </dependency>
</dependencies>
```

#### 使用说明

Remote框架通过简单的注解和接口定义，实现了声明式的API调用。以下是基本使用示例：

```java
import java.io.Serializable;
import lombok.Data;

// 定义响应数据结构
@Data
public class WeChatAccessTokenRes implements Serializable {
    @java.io.Serial
    private static final long serialVersionUID = -3722640486042138636L;
    private Integer errcode;
    private String errmsg;
    private String access_token;
    private Integer expires_in;
}

// 定义远程API接口
@Server(schema = "https", host = "api.weixin.qq.com", port = 443)
public interface WeChatAccessRemoting {
    // 使用@HttpMapping定义HTTP请求方法和路径
    // 使用@HttpQuery定义URL查询参数
    @HttpMapping(method = HttpMethod.GET, uri = "/cgi-bin/token", queries = @HttpQuery(name = "grant_type", value = "client_credential"))
    WeChatAccessTokenRes accessToken(@HttpQuery(name = "appid") String appid, @HttpQuery(name = "secret") String secret);
    
    // POST请求示例（JSON）
    @HttpMapping(method = HttpMethod.POST, uri = "/cgi-bin/message/custom/send")
    String sendMessage(@HttpQuery(name = "access_token") String accessToken, @JsonBody Object message);
}

// 创建代理并使用
public class WeChatApiTest {
    public static void main(String[] args) {
        // 创建远程接口代理实例
        WeChatAccessRemoting remoting = RemoteProxy.create(WeChatAccessRemoting.class);
        
        // 调用远程方法
        WeChatAccessTokenRes tokenRes = remoting.accessToken("your_appid", "your_secret");
        
        // 使用获取的access_token发送消息
        if (tokenRes.getErrcode() == null || tokenRes.getErrcode() == 0) {
            String result = remoting.sendMessage(tokenRes.getAccess_token(), 
                Map.of("touser", "user_openid", "msgtype", "text", 
                       "text", Map.of("content", "Hello World")));
            System.out.println("Message sent: " + result);
        }
    }
}
```

#### 框架特性

1. **声明式API调用**：通过简单的接口和注解定义，无需编写具体实现代码
2. **完整的生命周期管理**：提供丰富的回调接口，支持请求前后、调用、成功、失败、重试等各个环节的定制
3. **多种HTTP实现支持**：同时支持Netty和Apache HttpClient实现，可根据需要选择
4. **灵活的环境管理**：支持多环境配置，可以根据不同环境动态切换API服务器地址
5. **丰富的HTTP特性支持**：支持各种HTTP方法、请求头、查询参数、多种请求体格式（JSON、XML、Form等）
6. **与Spring无缝集成**：提供Spring集成模块，支持在Spring项目中使用自动注入等特性
7. **可扩展性**：核心架构设计开放，可以基于核心模块扩展其他功能

#### 高级特性

Remote框架支持更多高级特性，如：

- **自动重试机制**：使用@Retryable注解配置重试策略
- **SSL/TLS支持**：使用@Ssl注解配置SSL连接
- **请求响应拦截**：通过实现自定义的生命周期回调处理请求和响应
- **网络环境动态切换**：通过实现ApiServerRepository接口支持动态环境管理
- **多种响应解析器**：内置支持JSON、XML、文本等多种响应格式解析

#### 参与贡献

1. Fork 本仓库
2. 新建 Feat_xxx 分支
3. 提交代码（确保遵循项目的代码风格和命名规范）
4. 新建 Pull Request

#### 许可证

本项目采用MIT许可证 - 详见LICENSE文件

#### 技术栈

- Java 21
- Spring Boot 3.2.9
- Netty
- Apache HttpClient
- MyBatis-Plus / MyBatis-Flex
- Lombok
- Jackson