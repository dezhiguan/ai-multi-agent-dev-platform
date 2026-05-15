package com.example.agent.agents;

import com.example.agent.llm.LlmClient;
import com.example.agent.state.AgentState;
import com.example.agent.tools.FileTool;
import com.example.agent.tools.PromptUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CodeFixAgent implements BaseAgent {

    private final LlmClient llmClient;
    private final FileTool fileTool;

    @Override
    public AgentState execute(AgentState state) {
        System.out.println("=== CodeFixAgent 自动修复 ===");

        try {
            String errorLog = state.getTestResult().getReport();

            // 从提示词文件加载（统一规范！）
            String prompt = PromptUtil.fixPrompt(errorLog);

            // 调用大模型
            String fixedCode = llmClient.chat(prompt);

            // 覆盖写入
            fileTool.write(
                    "business-workspace/order-service/src/main/java/com/example/order/OrderController.java",
                    fixedCode
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
        return state;
    }


    @Override
    public String getAgentName() {
        return "CodeFixAgent";
    }
}