package com.example.agent.rag;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 向量存储服务（占位实现，不依赖任何pgvector库，项目可正常启动）
 * 后续对接LLM后再实现真实向量存储
 */
@Service
@RequiredArgsConstructor
public class VectorStoreService {

    private final Map<String, float[]> vectorMap = new HashMap();

    private final Map<String, String> contentMap = new HashMap<>();

    private final EmbeddingService embeddingService;

    // 模拟保存向量
    public void saveEmbedding(String taskId, String chunk, float[] embedding) {
        String key = UUID.randomUUID().toString();
        vectorMap.put(key, embedding);
        contentMap.put(key, chunk);
        System.out.println("【RAG】已存入知识块：" + chunk.substring(0, Math.min(30, chunk.length())) + "...");
    }

    // 模拟检索
    public List<String> searchSimilar(float[] queryEmbedding, int topK) {
        List<Map.Entry<String, Float>> scoreList = new ArrayList<>();
        for (String key : vectorMap.keySet()) {
            float sim = embeddingService.similarity(queryEmbedding, vectorMap.get(key));
            scoreList.add(new AbstractMap.SimpleEntry<>(key, sim));
        }

        scoreList.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        List<String> result = new ArrayList<>();
        for (int i = 0; i < Math.min(topK, scoreList.size()); i++) {
            String key = scoreList.get(i).getKey();
            result.add(contentMap.get(key));
        }
        return result;
    }
}