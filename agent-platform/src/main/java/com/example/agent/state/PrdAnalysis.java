package com.example.agent.state;
import lombok.Data;
@Data
public class PrdAnalysis {

    // 模块名、表结构、接口列表、页面列表
    private String queryContent;
    private String modules;
    private String tables;
    private String apis;
    private String pages;
}