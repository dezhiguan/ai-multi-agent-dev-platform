package com.example.agent.agents;


import com.example.agent.llm.LlmClient;
import com.example.agent.state.AgentState;
import com.example.agent.state.PrdAnalysis;
import com.example.agent.tools.PromptUtil;
import com.example.agent.util.JsonCleanUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PrdAgent implements BaseAgent {

    private final LlmClient llmClient;
    private final ObjectMapper objectMapper;


    @Override
    public AgentState execute(AgentState state) {
        System.out.println("=== PrdAgent 开始分析 PRD ===");

        try {

            //1.加载提示词
            String prompt = PromptUtil.buildPrompt("prd-agent.txt",
                    state.getPrdContent() == null ? "" : state.getPrdContent());
            //2.调用llm
            String rawResp = llmClient.chat(prompt);
            String json = JsonCleanUtil.cleanJson(rawResp);

            //3.解析结构化结果
            PrdAnalysis prdAnalysis = objectMapper.readValue(json, PrdAnalysis.class);

            //4.写入全局状态
            state.setPrdAnalysis(prdAnalysis);

            System.out.println("=== PrdAgent 分析完成 ===");

        } catch (Exception e) {
            state.addError("PRD分析失败：" + e.getMessage());
            e.printStackTrace();
        }

        return state;
    }

    @Override
    public String getAgentName() {
        return "PrdAgent";
    }
}
