package com.example.agent.controller;

import com.example.agent.workflow.AgentSupervisor;
import com.example.agent.workflow.WorkflowContext;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 工作流对外HTTP接口
 */
@RestController
@RequestMapping("/workflow")
@RequiredArgsConstructor
public class WorkflowController {

    private final AgentSupervisor agentSupervisor;

    /**
     * 启动一个任务
     */
    @GetMapping("/start/{taskId}")
    public WorkflowContext start(@PathVariable String taskId) {
        return agentSupervisor.startWorkflow(taskId);
    }
}