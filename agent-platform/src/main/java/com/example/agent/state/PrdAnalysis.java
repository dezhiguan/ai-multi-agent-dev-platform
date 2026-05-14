package com.example.agent.state;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PrdAnalysis {

    // 给RAG用的查询内容
    private String queryContent;

    // 👇 全部改成 数组/List 格式，匹配大模型返回！
    private List<String> modules;
    private List<Map<String, Object>> tables;
    private List<String> apis;
    private List<String> pages;
}