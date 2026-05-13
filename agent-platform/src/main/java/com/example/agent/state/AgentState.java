package com.example.agent.state;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * 全局共享状态
 * 所有Agent之间不直接通信，只读写这个对象
 */
@Data
public class AgentState {

    // 任务唯一标识
    private String taskId;

    // PRD分析结果（PRDAgent写入）
    private PrdAnalysis prdAnalysis;

    // RAG检索结果（RagAgent写入）
    private RagContext ragContext;

    // 后端代码生成结果
    private CodeGenerationResult backendCodeResult;

    // 前端代码生成结果
    private CodeGenerationResult frontendCodeResult;

    // 测试执行结果
    private TestExecutionResult testResult;

    // 服务启动结果
    private RuntimeResult runtimeResult;

    // 错误信息列表
    private List<String> errorList = new ArrayList<>();

    // 快速添加错误
    public void addError(String error) {
        this.errorList.add(error);
    }
}