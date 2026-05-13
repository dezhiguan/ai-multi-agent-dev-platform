package com.example.agent.rag;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * 向量存储服务（占位实现，不依赖任何pgvector库，项目可正常启动）
 * 后续对接LLM后再实现真实向量存储
 */
@Service
@RequiredArgsConstructor
public class VectorStoreService {

    // 暂存模拟数据
    private final List<String> mockContents = new ArrayList<>();

    // 模拟保存向量
    public void saveEmbedding(String taskId, String chunk, float[] embedding) {
        System.out.println("模拟保存向量：" + chunk);
        mockContents.add(chunk);
    }

    // 模拟检索
    public List<String> searchSimilar(float[] queryEmbedding, int topK) {
        System.out.println("模拟向量检索，返回前" + topK + "条结果");
        return mockContents.stream()
                .limit(topK)
                .toList();
    }
}