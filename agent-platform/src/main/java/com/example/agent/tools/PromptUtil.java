package com.example.agent.tools;

import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class PromptUtil {


    // 通用：加载提示词模板
    public static String load(String templateName) {
        try (InputStream inputStream = new ClassPathResource("prompts/" + templateName).getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "";
        }
    }

    // PRD 分析
    public static String prdPrompt(String prdContent) {
        return load("prd-agent.txt").replace("{{prdContent}}", prdContent);
    }

    // 后端生成
    public static String backendPrompt(String prdInfo, String ragInfo) {
        return load("backend-code-agent.txt")
                .replace("{{prdAnalysis}}", prdInfo)
                .replace("{{ragContext}}", ragInfo);
    }

    // 前端生成
    public static String frontendPrompt(String prdInfo) {
        return load("frontend-code-agent.txt").replace("{{prdAnalysis}}", prdInfo);
    }

    // 测试用例生成
    public static String testPrompt(String backendCode) {
        return load("test-agent.txt")
                .replace("{{backendCode}}", backendCode);
    }

    // 代码修复

    public static String fixPrompt(String backendCode,
                                   String errorLog) {
        return load("code-fix-agent.txt")
                .replace("{{backendCode}}", backendCode)
                .replace("{{errorLog}}", errorLog);
    }

}
