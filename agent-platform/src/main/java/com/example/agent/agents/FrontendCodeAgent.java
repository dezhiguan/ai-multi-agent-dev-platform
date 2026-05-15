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


@Component
@RequiredArgsConstructor
public class FrontendCodeAgent implements BaseAgent{

    private final LlmClient llmClient;
    private final FileTool fileTool;
    private final ObjectMapper objectMapper;

    @Override
    public AgentState execute(AgentState state) {
        System.out.println("=== FrontendCodeAgent 开始真实生成前端代码 ===");

        try {
            String prdInfo = new ObjectMapper().writeValueAsString(state.getPrdAnalysis());
            String prompt = PromptUtil.frontendPrompt(prdInfo);

            String raw = llmClient.chat(prompt);
            String json = JsonCleanUtil.cleanJson(raw);
            Map<String, String> codeMap = objectMapper.readValue(json, Map.class);

            String base = "business-workspace/order-web/src/";
            for (Map.Entry<String, String> entry : codeMap.entrySet()) {
                fileTool.write(base + entry.getKey(), entry.getValue());
                System.out.println("写入前端：" + entry.getKey());
            }

            state.getFrontendCodeResult().setSuccess(true);
            System.out.println("=== 前端代码全部生成完成 ===");
        } catch (Exception e) {
            e.printStackTrace();
            state.addError("前端生成失败：" + e.getMessage());
        }
        return state;
    }

    @Override
    public String getAgentName() {
        return "Override";
    }
}
