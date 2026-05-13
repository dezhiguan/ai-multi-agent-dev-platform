package com.example.agent.workflow;


import com.example.agent.state.AgentState;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * 工作流上下文
 * 保存整个流程的执行状态
 */
@Data
public class WorkflowContext {

    // 任务ID
    private String taskId;

    // 当前执行步骤
    private WorkflowStep currentStep;

    // 全局共享状态（所有Agent读写）
    private AgentState agentState;

    // 已完成的步骤
    private List<WorkflowStep> finishedSteps = new ArrayList<>();

    // 是否执行失败
    private boolean hasError = false;

    // 错误信息
    private String errorMsg;
}