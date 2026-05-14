package com.example.agent.util;

public class JsonCleanUtil {

    /**
     * 清洗大模型返回内容，去掉 ```json ``` 等标记，只保留纯JSON
     */
    public static String cleanJson(String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }
        String str = raw.replaceAll("```json", "").replaceAll("```", "").trim();
        int start = str.indexOf("{");
        int end = str.lastIndexOf("}");
        if (start >= 0 && end >= 0) {
            return str.substring(start, end + 1);
        }
        return str;
    }
}