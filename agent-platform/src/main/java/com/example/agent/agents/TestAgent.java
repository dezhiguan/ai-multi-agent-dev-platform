package com.example.agent.agents;

import com.example.agent.llm.LlmClient;
import com.example.agent.state.AgentState;
import com.example.agent.tools.FileTool;
import com.example.agent.tools.MavenTool;
import com.example.agent.tools.PromptUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestAgent implements BaseAgent {

    private final LlmClient llmClient;
    private final FileTool fileTool;
    private final MavenTool mavenTool;

    @Override
    public AgentState execute(AgentState state) {
        System.out.println("=== TestAgent 开始生成单元测试 ===");

        try {
            // 从提示词文件加载（统一规范！）
            String prompt = PromptUtil.testPrompt();

            // 调用大模型
            String testCode = llmClient.chat(prompt);

            // 写入文件
            fileTool.write(
                    "business-workspace/order-service/src/test/java/com/example/order/OrderControllerTest.java",
                    testCode
            );

            // 执行测试
            String report = mavenTool.test("business-workspace/order-service");
            state.getTestResult().setReport(report);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return state;
    }


    @Override
    public String getAgentName() {
        return "TestAgent";
    }
}