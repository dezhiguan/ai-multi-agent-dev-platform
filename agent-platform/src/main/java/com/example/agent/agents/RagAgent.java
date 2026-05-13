package com.example.agent.agents;

import com.example.agent.rag.RagSearchService;
import com.example.agent.state.AgentState;
import com.example.agent.state.RagContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RagAgent implements BaseAgent {

    private final RagSearchService ragSearchService;

    @Override
    public AgentState execute(AgentState state) {
        System.out.println("=== RagAgent 开始检索知识库 ===");

        //1.从PRD分析结果取查询值
        String queryContent = state.getPrdAnalysis().getQueryContent();

        //2.检索知识
        List<String> docs = ragSearchService.search(queryContent);

        //3.写回共享状态
        RagContext ragContext = new RagContext();
        ragContext.setKnowledgeDocs(docs);
        state.setRagContext(ragContext);

        System.out.println("=== RagAgent 检索完成，找到：" + docs.size() + "条知识 ===");

        return state;
    }

    @Override
    public String getAgentName() {
        return "RagAgent";
    }
}
