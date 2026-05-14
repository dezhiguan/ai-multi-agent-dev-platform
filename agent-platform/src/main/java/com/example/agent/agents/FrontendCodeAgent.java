package com.example.agent.agents;

import com.example.agent.llm.LlmClient;
import com.example.agent.state.AgentState;
import com.example.agent.tools.FileTool;
import com.example.agent.tools.PromptUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class FrontendCodeAgent implements BaseAgent{

    private final LlmClient llmClient;
    private final FileTool fileTool;

    @Override
    public AgentState execute(AgentState state) {
        System.out.println("=== FrontendCodeAgent 开始生成前端代码 ===");

        //1.构建提示词
        try {
            String prompt = PromptUtil.buildPrompt("frontend-code-agent.txt",
                    state.getPrdAnalysis() != null ? state.getPrdAnalysis().toString() : "",
                    state.getRagContext() != null ? state.getRagContext().toString() : "");

            //2.llm生成代码
            String code = llmClient.chat(prompt);

            //3.写入文件
            String path = "business-workspace/order-web/src/pages/OrderPage.tsx";
            fileTool.write(path, code);

            //4.更新状态
            state.getFrontendCodeResult().setSuccess(true);
            state.getFrontendCodeResult().setCodePath(path);

            System.out.println("=== 前端代码生成完成：" + path);
        } catch (Exception e) {
            state.addError("前端生成失败：" + e.getMessage());
            e.printStackTrace();
        }





        return null;
    }

    @Override
    public String getAgentName() {
        return "Override";
    }
}
