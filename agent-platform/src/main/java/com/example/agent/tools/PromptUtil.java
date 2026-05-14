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
}
