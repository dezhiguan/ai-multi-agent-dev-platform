package com.example.agent.agents;

import com.example.agent.llm.LlmClient;
import com.example.agent.state.AgentState;
import com.example.agent.tools.FileTool;
import com.example.agent.tools.PromptUtil;
import com.example.agent.util.JsonCleanUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CodeFixAgent implements BaseAgent {

    private final LlmClient llmClient;
    private final FileTool fileTool;
    private final ObjectMapper objectMapper;

    private static final Set<String> ALLOWED_FILES =
            Set.of(
                    "Order",
                    "OrderRepository",
                    "OrderCreateRequest",
                    "OrderDTO",
                    "OrderService",
                    "OrderServiceImpl",
                    "OrderController"
            );


    @Override
    public AgentState execute(AgentState state) {
        System.out.println("=== CodeFixAgent 自动修复 ===");

        try {
            String errorLog = state.getTestResult().getReport();
            String backendCode = readBackendCode();

            // 从提示词文件加载（统一规范！）
            String prompt = PromptUtil.fixPrompt(backendCode,
                    errorLog);

            // 调用大模型
            String raw = llmClient.chat(prompt);
            String json = JsonCleanUtil.cleanJson(raw);
            Map<String, String> fixedCodeMap =
                    objectMapper.readValue(json, Map.class);
            validateFixedCode(fixedCodeMap);

            String base = "business-workspace/order-service/src/main/java/com/example/order/";

            for (Map.Entry<String, String> entry : fixedCodeMap.entrySet()) {
                fileTool.write(base + entry.getKey() + ".java",
                        entry.getValue());
                System.out.println("修复写入：" + entry.getKey());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return state;
    }

    private void validateFixedCode(Map<String, String>
                                           fixedCodeMap) {
        if (fixedCodeMap == null ||
                fixedCodeMap.isEmpty()) {
            throw new IllegalArgumentException("修复结果为空");
        }

        for (Map.Entry<String, String> entry :
                fixedCodeMap.entrySet()) {
            String fileName = entry.getKey();
            String content = entry.getValue();

            if (!ALLOWED_FILES.contains(fileName)) {
                throw new IllegalArgumentException("修复结果包含非法文件：" + fileName);
            }

            if (content == null || content.isBlank()) {
                throw new
                        IllegalArgumentException(fileName + " 修复内容为空");
            }

            if (content.contains("```")) {
                throw new
                        IllegalArgumentException(fileName + " 修复内容包含Markdown 代码块");
            }

            if (!content.contains("package com.example.order;")) {
                throw new
                        IllegalArgumentException(fileName + " 包名不正确");
            }
        }
    }

    private String readBackendCode() throws Exception {
        String order = fileTool.read("business-workspace/order-service/src/main/java/com/example/order/Order.java");
        String orderRepository = fileTool.read("business-workspace/order-service/src/main/java/com/example/order/OrderRepository.java");
        String orderCreateRequest =
                fileTool.read("business-workspace/order-service/src/main/java/com/example/order/OrderCreateRequest.java");
        String orderDTO = fileTool.read("business-workspace/order-service/src/main/java/com/example/order/OrderDTO.java");
        String orderService = fileTool.read("business-workspace/order-service/src/main/java/com/example/order/OrderService.java");
        String orderServiceImpl = fileTool.read("business-workspace/order-service/src/main/java/com/example/order/OrderServiceImpl.java");
        String orderController = fileTool.read("business-workspace/order-service/src/main/java/com/example/order/OrderController.java");

        return "===== Order.java =====\n"
                + order + "\n\n"
                + "===== OrderRepository.java =====\n"
                + orderRepository + "\n\n"
                + "===== OrderCreateRequest.java =====\n"
                + orderCreateRequest + "\n\n"
                + "===== OrderDTO.java =====\n"
                + orderDTO + "\n\n"
                + "===== OrderService.java =====\n"
                + orderService + "\n\n"
                + "===== OrderServiceImpl.java =====\n"
                + orderServiceImpl + "\n\n"
                + "===== OrderController.java =====\n"
                + orderController;
    }

    @Override
    public String getAgentName() {
        return "CodeFixAgent";
    }
}