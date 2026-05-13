package com.example.agent.controller;

import com.example.agent.tools.FileTool;
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

    private final FileTool fileTool;

    /**
     * 启动一个任务
     */
    @GetMapping("/start/{taskId}")
    public WorkflowContext start(@PathVariable String taskId) {
        return agentSupervisor.startWorkflow(taskId);
    }

    @GetMapping("/test-tool")
    public String testTool() throws Exception {
        fileTool.write(
                "business-workspace/test.txt",
                "这是 Day5 Tool 生成的内容"
        );
        return "文件写入成功！";
    }
}