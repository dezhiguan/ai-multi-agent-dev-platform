package com.example.agent.agents;

import com.example.agent.llm.LlmClient;
import com.example.agent.state.AgentState;
import com.example.agent.tools.FileTool;
import com.example.agent.tools.PromptUtil;
import com.example.agent.util.JsonCleanUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;


@Component
@RequiredArgsConstructor
public class BackendCodeAgent implements BaseAgent {

    private final LlmClient llmClient;
    private final FileTool fileTool;
    private final ObjectMapper objectMapper;

    private static final Set<String> REQUIRED_FILES = Set.of(
            "Order",
            "OrderRepository",
            "OrderCreateRequest",
            "OrderDTO",
            "OrderService",
            "OrderServiceImpl",
            "OrderController"
    );



    @Override
    public AgentState execute(AgentState state) {

        System.out.println("=== BackendCodeAgent 开始真实生成后端代码 ===");

        try {
            // 1. 构建提示词
            String prdInfo = objectMapper.writeValueAsString(state.getPrdAnalysis());
            String ragInfo = state.getRagContext() != null ? state.getRagContext().toString() : "无规范";
            String prompt = PromptUtil.backendPrompt(prdInfo, ragInfo);

            // 2. 调用大模型
            String raw = llmClient.chat(prompt);

            String json = JsonCleanUtil.cleanJson(raw);
            Map<String, String> codeMap = objectMapper.readValue(json,
                    Map.class);
            validateGeneratedCode(codeMap);

            // 3. 批量写入文件
            String base = "business-workspace/order-service/src/main/java/com/example/order/";
            for (Map.Entry<String, String> entry : codeMap.entrySet()) {
                fileTool.write(base + entry.getKey() + ".java", entry.getValue());
                System.out.println("写入：" + entry.getKey());
            }

            // 4. 写入 pom.xml
            fileTool.write("business-workspace/order-service/pom.xml", """
                    <?xml version="1.0" encoding="UTF-8"?>
                    <project xmlns="http://maven.apache.org/POM/4.0.0"
                             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                             xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                    
                        <modelVersion>4.0.0</modelVersion>
                    
                        <parent>
                            <groupId>org.springframework.boot</groupId>
                            <artifactId>spring-boot-starter-parent</artifactId>
                            <version>3.2.0</version>
                            <relativePath/>
                        </parent>
                    
                        <groupId>com.example</groupId>
                        <artifactId>order-service</artifactId>
                        <version>0.0.1-SNAPSHOT</version>
                        <name>order-service</name>
                    
                        <properties>
                            <java.version>17</java.version>
                        </properties>
                    
                        <dependencies>
                            <dependency>
                                <groupId>org.springframework.boot</groupId>
                                <artifactId>spring-boot-starter-web</artifactId>
                            </dependency>
                    
                            <dependency>
                                <groupId>org.springframework.boot</groupId>
                                <artifactId>spring-boot-starter-data-jpa</artifactId>
                            </dependency>
                    
                            <dependency>
                                <groupId>com.h2database</groupId>
                                <artifactId>h2</artifactId>
                                <scope>runtime</scope>
                            </dependency>
                    
                            <dependency>
                                <groupId>org.projectlombok</groupId>
                                <artifactId>lombok</artifactId>
                                <optional>true</optional>
                            </dependency>
                    
                            <dependency>
                                <groupId>org.springframework.boot</groupId>
                                <artifactId>spring-boot-starter-test</artifactId>
                                <scope>test</scope>
                            </dependency>
                        </dependencies>
                    
                        <build>
                            <plugins>
                                <plugin>
                                    <groupId>org.springframework.boot</groupId>
                                    <artifactId>spring-boot-maven-plugin</artifactId>
                                </plugin>
                            </plugins>
                        </build>
                    </project>
                    """);

            // 5. 写入 Spring Boot 启动入口
            fileTool.write("business-workspace/order-service/src/main/java/com/example/order/OrderServiceApplication.java", """
                    package com.example.order;
                    
                    import org.springframework.boot.SpringApplication;
                    import org.springframework.boot.autoconfigure.SpringBootApplication;
                    
                    @SpringBootApplication
                    public class OrderServiceApplication {
                    
                        public static void main(String[] args) {
                            SpringApplication.run(OrderServiceApplication.class, args);
                        }
                    }
                    """);
            // 6. 写入运行配置
            fileTool.write("business-workspace/order-service/src/main/resources/application.yml", """
                    server:
                      port: 8090
                    
                    spring:
                      datasource:
                        url: jdbc:h2:mem:order_service;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
                        driver-class-name: org.h2.Driver
                        username: sa
                        password:
                    
                      jpa:
                        hibernate:
                          ddl-auto: update
                        show-sql: false
                    
                      h2:
                        console:
                          enabled: true
                    """);

            state.getBackendCodeResult().setSuccess(true);
            System.out.println("=== 后端代码全部生成完成 ===");

        } catch (Exception e) {
            e.printStackTrace();
            state.addError("后端生成失败：" + e.getMessage());
        }
        return state;
    }

    @Override
    public String getAgentName() {
        return "BackendCodeAgent";
    }

    private void validateGeneratedCode(Map<String, String> codeMap) {
        if (!codeMap.keySet().equals(REQUIRED_FILES)) {
            throw new IllegalArgumentException("后端生成文件不完整或包含非法文件，实际文件：" + codeMap.keySet());
        }

        for (Map.Entry<String, String> entry : codeMap.entrySet()) {
            String fileName = entry.getKey();
            String content = entry.getValue();

            if (content == null || content.isBlank()) {
                throw new IllegalArgumentException(fileName + " 内容为空");
            }

            if (content.contains("```")) {
                throw new IllegalArgumentException(fileName + " 包含Markdown 代码块");
            }

            if (!content.contains("package com.example.order;")) {
                throw new IllegalArgumentException(fileName + " 包名不正确，必须是 com.example.order");
            }

            if (content.contains("com.example.demo") ||
                    content.contains("com.example.ordermanagement")) {
                throw new IllegalArgumentException(fileName + " 包含错误包名");
            }
        }
    }
}
