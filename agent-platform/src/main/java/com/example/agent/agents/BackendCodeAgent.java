package com.example.agent.agents;

import com.example.agent.llm.LlmClient;
import com.example.agent.state.AgentState;
import com.example.agent.tools.FileTool;
import com.example.agent.tools.PromptUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class BackendCodeAgent implements BaseAgent {

    private final LlmClient llmClient;
    private final FileTool fileTool;

    @Override
    public AgentState execute(AgentState state) {

        //1.构造提示词
        try {
            String prompt = PromptUtil.buildPrompt("backend-code-agent.txt",
                    state.getPrdAnalysis().toString(),
                    state.getRagContext() != null ? state.getRagContext().toString() : "无规范");

            //2.调用llm生成代码
            String code = llmClient.chat(prompt);

            //3.写入文件
            String path = "business-workspace/order-service/src/main/java/com/example/order/OrderController.java";
            fileTool.write(path,code);

            //4.更新状态
            state.getBackendCodeResult().setSuccess(true);
            state.getBackendCodeResult().setCodePath(path);
            System.out.println("=== 后端代码生成完成：" + path);

        } catch (Exception e) {
            state.addError("后端代码生成失败：" + e.getMessage());
            e.printStackTrace();
        }
        return state;
    }

    @Override
    public String getAgentName() {
        return "BackendCodeAgent";
    }
}
