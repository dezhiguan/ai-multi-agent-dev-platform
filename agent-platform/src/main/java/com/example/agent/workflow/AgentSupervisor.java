package com.example.agent.workflow;

import com.example.agent.agents.*;
import com.example.agent.state.*;
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

    private final FrontendCodeAgent frontendCodeAgent;

    private final TestAgent testAgent;

    private final CodeFixAgent codeFixAgent;

    private final RuntimeAgent runtimeAgent;

    private static final int MAX_FIX_RETRY = 2;

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

        // 初始化PRD
        agentState.setPrdContent("开发订单管理系统，包含创建、列表、详情");
        PrdAnalysis prdAnalysis = new PrdAnalysis();
        prdAnalysis.setQueryContent("订单管理系统");
        agentState.setPrdAnalysis(prdAnalysis);

        // 初始化RAG
        RagContext ragContext = new RagContext();
        agentState.setRagContext(ragContext);

        // 初始化前后端生成结果
        agentState.setBackendCodeResult(new CodeGenerationResult());
        agentState.setFrontendCodeResult(new CodeGenerationResult());

        // 初始化测试、运行结果
        agentState.setTestResult(new TestExecutionResult());
        agentState.setRuntimeResult(new RuntimeResult());

        context.setTaskId(taskId);
        context.setAgentState(agentState);

        executeAndRecord(context, WorkflowStep.PRD_ANALYSIS);
        executeAndRecord(context, WorkflowStep.RAG_RETRIEVE);
        executeAndRecord(context,
                WorkflowStep.BACKEND_CODE_GENERATE);
        executeAndRecord(context,
                WorkflowStep.FRONTEND_CODE_GENERATE);

        executeAndRecord(context, WorkflowStep.TEST_EXECUTE);

        int fixCount = 0;
        while (!
                context.getAgentState().getTestResult().isPass() &&
                fixCount < MAX_FIX_RETRY) {
            executeAndRecord(context, WorkflowStep.CODE_FIX);
            executeAndRecord(context,
                    WorkflowStep.TEST_EXECUTE);
            fixCount++;
        }

        if (context.getAgentState().getTestResult().isPass()) {
            executeAndRecord(context, WorkflowStep.RUNTIME_START);
        } else {
            context.setHasError(true);
            context.setErrorMsg("自动修复后测试仍未通过，已停止启动服务");
        }

        return context;
    }

    private void executeAndRecord(WorkflowContext context,
                                  WorkflowStep step) {
        if (context.isHasError()) {
            return;
        }

        context.setCurrentStep(step);
        executeStep(context);
        context.getFinishedSteps().add(step);

        if (!context.getAgentState().getErrorList().isEmpty()) {
            context.setHasError(true);

            context.setErrorMsg(
                    context.getAgentState().getErrorList()
                            .get(context.getAgentState().getErrorList().size() - 1)
            );
        }
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

        if (step == WorkflowStep.PRD_ANALYSIS) {
            prdAgent.execute(state);
        }

        if (step == WorkflowStep.RAG_RETRIEVE) {
            ragAgent.execute(state);
        }

        if (step == WorkflowStep.BACKEND_CODE_GENERATE) {
            backendCodeAgent.execute(state);
        }

        if (step == WorkflowStep.FRONTEND_CODE_GENERATE) {
            frontendCodeAgent.execute(state);
        }

        if (step == WorkflowStep.TEST_EXECUTE) {
            testAgent.execute(state);
        }

        if (step == WorkflowStep.CODE_FIX) {
            codeFixAgent.execute(state);
        }

        if (step == WorkflowStep.RUNTIME_START) {
            runtimeAgent.execute(state);
        }


    }
}