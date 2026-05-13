package com.example.agent.workflow;

import lombok.Getter;

/**
 * 工作流步骤枚举
 * 定义整个AI自动化开发的全流程
 */
@Getter
public enum WorkflowStep {

    PRD_ANALYSIS("PRD需求分析"),
    RAG_RETRIEVE("知识库检索"),
    BACKEND_CODE_GENERATE("后端代码生成"),
    FRONTEND_CODE_GENERATE("前端代码生成"),
    TEST_EXECUTE("自动化测试"),
    CODE_FIX("自动修复代码"),
    RUNTIME_START("服务启动");

    private final String desc;

    WorkflowStep(String desc) {
        this.desc = desc;
    }
}