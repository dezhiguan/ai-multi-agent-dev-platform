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
        System.out.println("=== CodeFixAgent 开始自动修复代码 ===");

        // 模拟取错误日志，后面可接入真实mvn日志
        String errorLog = "单元测试编译异常、接口参数不匹配";

        // 构造提示词
        try {
            String prompt = PromptUtil.buildPrompt(
                    "code-fix-agent.txt",
                    "订单后端Controller代码",
                    errorLog
            );

            //llm修复后的代码
            String fixedCode = llmClient.chat(prompt);

            // 覆盖写入原文件
            String path = "business-workspace/order-service/src/main/java/com/example/order/OrderController.java";
            fileTool.write(path, fixedCode);

            System.out.println("=== 代码修复完成，已覆盖原文件 ===");
        } catch (Exception e) {
            state.addError("代码修复失败：" + e.getMessage());
            e.printStackTrace();
        }
        return state;
    }

    @Override
    public String getAgentName() {
        return "CodeFixAgent";
    }
}
