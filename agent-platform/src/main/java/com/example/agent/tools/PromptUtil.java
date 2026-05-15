package com.example.agent.tools;

import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class PromptUtil {

    public static String buildPrompt(String templateName, String prdContent) throws Exception {
        // 从 classpath 读取文件（兼容开发 & 生产）
        try (InputStream inputStream = new ClassPathResource("prompts/" + templateName).getInputStream()) {
            String template = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return template.replace("{{prdContent}}", prdContent);
        }
    }

    public static String buildPrompt(String templateName, String prdContent, String ragContext)
            throws Exception {
        // 从 classpath 读取文件（兼容开发 & 生产）
        try (InputStream inputStream = new ClassPathResource("prompts/" + templateName).getInputStream()) {
            String template = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return template.replace("{{prdContent}}", prdContent)
                    .replace("{{ragContext}}", ragContext);
        }
    }

    public static String buildBackendPrompt(String prdInfo, String ragInfo) {
        return """
    你是资深后端工程师。根据需求和规范，生成一整套可直接运行的SpringBoot代码。
    
    需求：
    """ + prdInfo + """
    
    规范：
    """ + ragInfo + """
    
    必须生成以下所有文件，返回JSON格式，key=文件名，value=代码：
    {
        "OrderController": "...",
        "OrderService": "...",
        "OrderServiceImpl": "...",
        "Order": "...",
        "OrderRepository": "...",
        "OrderDTO": "...",
        "OrderCreateRequest": "..."
    }
    
    要求：
    - 带Lombok
    - 带JPA
    - 带REST接口
    - 可直接编译
    - 只返回JSON，不要解释
    """;
    }

    public static String buildFrontendPrompt(String prdInfo) {
        return """
    你是资深前端工程师，生成React + TypeScript完整项目。
    
    需求：
    """ + prdInfo + """
    
    生成文件：
    {
        "pages/OrderPage.tsx": "...",
        "components/OrderTable.tsx": "...",
        "components/OrderForm.tsx": "...",
        "api/orderApi.ts": "...",
        "types/order.ts": "..."
    }
    
    只返回JSON，不要解释。
    """;
    }
}
