package com.example.agent.state;

import lombok.Data;

@Data
public class PrdAnalysis {
    // 给RAG用的查询内容
    private String queryContent;

    // 👇 全部改成 String，永远不报错！
    private String modules;    // 大模型返回字符串，不再是数组
    private String tables;     // 大模型返回字符串
    private String apis;       // 大模型返回字符串
    private String pages;      // 大模型返回字符串
}