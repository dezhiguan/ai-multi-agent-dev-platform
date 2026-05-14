package com.example.agent.workflow;

import com.example.agent.agents.BackendCodeAgent;
import com.example.agent.agents.BaseAgent;
import com.example.agent.agents.PrdAgent;
import com.example.agent.agents.RagAgent;
import com.example.agent.state.AgentState;
import com.example.agent.state.CodeGenerationResult;
import com.example.agent.state.PrdAnalysis;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * 多Agent中央调度器
 * 作用：按顺序管理、执行所有Agent
 */
@Component
@RequiredArgsConstructor
public class AgentSupervisor {

    private final PrdAgent prdAgent;

    private final RagAgent ragAgent;

    private final BackendCodeAgent backendCodeAgent;

    // 自动注入所有实现了BaseAgent的Agent
    private final List<BaseAgent> agentList;

    // 工作流步骤顺序（固定执行流程）
    private final List<WorkflowStep> workflowSequence = new ArrayList<>();

    @PostConstruct
    public void initWorkflow() {
        // 定义执行顺序
        workflowSequence.add(WorkflowStep.PRD_ANALYSIS);
        workflowSequence.add(WorkflowStep.RAG_RETRIEVE);
        workflowSequence.add(WorkflowStep.BACKEND_CODE_GENERATE);
        workflowSequence.add(WorkflowStep.FRONTEND_CODE_GENERATE);
        workflowSequence.add(WorkflowStep.TEST_EXECUTE);
        workflowSequence.add(WorkflowStep.CODE_FIX);
        workflowSequence.add(WorkflowStep.RUNTIME_START);
    }

    /**
     * 启动整个工作流
     */
    public WorkflowContext startWorkflow(String taskId) {
        WorkflowContext context = new WorkflowContext();
        AgentState agentState = new AgentState();
        agentState.setTaskId(taskId);

        // ====================== 修复 1：给 PRD 内容默认值（必加）======================
        agentState.setPrdContent("我需要开发一个订单管理系统，包含创建订单、订单列表、订单详情功能");

        // ====================== 修复 2：初始化 PrdAnalysis（防空指针）======================
        PrdAnalysis prdAnalysis = new PrdAnalysis();
        prdAnalysis.setQueryContent("订单管理系统，包含创建、查询、列表功能");
        agentState.setPrdAnalysis(prdAnalysis);

        // ====================== 修复 3：初始化后端结果对象（防空指针）======================
        CodeGenerationResult backendResult = new CodeGenerationResult();
        agentState.setBackendCodeResult(backendResult);

        context.setTaskId(taskId);
        context.setAgentState(agentState);

        // 执行所有步骤
        for (WorkflowStep step : workflowSequence) {
            if (context.isHasError()) break;

            context.setCurrentStep(step);
            executeStep(context);
            context.getFinishedSteps().add(step);
        }

        return context;
    }

    /**
     * 执行单个步骤
     */
    private void executeStep(WorkflowContext context) {
        AgentState state = context.getAgentState();
        WorkflowStep step = context.getCurrentStep();

        System.out.println("=====================================");
        System.out.println("正在执行步骤：" + step.getDesc());
        System.out.println("=====================================");

        if(step == WorkflowStep.PRD_ANALYSIS) {
            prdAgent.execute(state);
        }

        if(step == WorkflowStep.RAG_RETRIEVE) {
            ragAgent.execute(state);
        }

        if(step == WorkflowStep.BACKEND_CODE_GENERATE) {
            backendCodeAgent.execute(state);
        }

    }
}