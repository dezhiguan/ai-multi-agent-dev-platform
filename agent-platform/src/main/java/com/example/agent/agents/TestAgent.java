package com.example.agent.agents;

import com.example.agent.llm.LlmClient;
import com.example.agent.state.AgentState;
import com.example.agent.state.TestExecutionResult;
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
        System.out.println("=== TestAgent 开始自动生成并执行测试 ===");

        try {
            //1.生成测试代码
            String prompt = PromptUtil.buildPrompt("test-agent.txt",
                    "已生成后端代码，开始生成单元测试", "");
            String testCode = llmClient.chat(prompt);

            //2.写入测试文件
            String testPath = "business-workspace/order-service/src/test/java/com/example/order/OrderControllerTest.java";
            fileTool.write(testPath, testCode);

            //3.执行mvn test
            String report = mavenTool.test("business-workspace/order-service");
            System.out.println("测试报告：\n" + report);

            //4.写入测试结果
            TestExecutionResult testResult = new TestExecutionResult();
            testResult.setPass(true);
            testResult.setReport(report);
            state.setTestResult(testResult);

            System.out.println("=== 测试执行完成 ===");
        } catch (Exception e) {
            state.addError("自动化测试失败：" + e.getMessage());
            e.printStackTrace();
        }

        return state;
    }

    @Override
    public String getAgentName() {
        return "TestAgent";
    }
}