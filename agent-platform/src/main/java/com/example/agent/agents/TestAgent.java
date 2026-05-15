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
            String backendCode = readBackendCode();

            // 从提示词文件加载（统一规范！）
            String prompt = PromptUtil.testPrompt(backendCode);

            // 调用大模型
            String testCode = llmClient.chat(prompt);
            validateTestCode(testCode);

            // 写入文件
            fileTool.write(
                    "business-workspace/order-service/src/test/java/com/example/order/OrderControllerTest.java",
                    testCode
            );

            // 执行测试
            String report = mavenTool.test("business-workspace/order-service");
            state.getTestResult().setReport(report);
            state.getTestResult().setPass(report.startsWith("exitCode=0"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return state;
    }

    private String readBackendCode() throws Exception {
        String orderController = fileTool.read("business-workspace/order-service/src/main/java/com/example/order/OrderController.java");
        String orderService = fileTool.read("business-workspace/order-service/src/main/java/com/example/order/OrderService.java");
        String orderDTO = fileTool.read("business-workspace/order-service/src/main/java/com/example/order/OrderDTO.java");
        String orderCreateRequest = fileTool.read("business-workspace/order-service/src/main/java/com/example/order/OrderCreateRequest.java");

        return "===== OrderController.java =====\n"
                + orderController + "\n\n"
                + "===== OrderService.java =====\n"
                + orderService + "\n\n"
                + "===== OrderDTO.java =====\n"
                + orderDTO + "\n\n"
                + "===== OrderCreateRequest.java =====\n"
                + orderCreateRequest;
    }

    private void validateTestCode(String testCode) {
        if (testCode == null || testCode.isBlank()) {
            throw new IllegalArgumentException("测试代码为空");
        }

        if (testCode.contains("```")) {
            throw new IllegalArgumentException("测试代码包含 Markdown 代码块");
        }

        if (!testCode.contains("package com.example.order;")) {
            throw new IllegalArgumentException("测试代码包名不正确");
        }

        if (!testCode.contains("class OrderControllerTest")) {
            throw new IllegalArgumentException("测试类名必须是 OrderControllerTest");
        }

        if (testCode.contains("YourController")
                || testCode.contains("YourService")
                || testCode.contains("YourEntity")) {
            throw new IllegalArgumentException("测试代码包含模板占位类名");
        }
    }


    @Override
    public String getAgentName() {
        return "TestAgent";
    }
}