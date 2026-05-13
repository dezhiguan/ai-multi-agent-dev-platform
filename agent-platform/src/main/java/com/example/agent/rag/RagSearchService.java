package com.example.agent.rag;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RagSearchService {

    private final VectorStoreService vectorStoreService;

    private final EmbeddingService embeddingService;


    //输入问题 -> 检索相关知识
   public List<String> search(String query){
       float[] embedding = embeddingService.createEmbedding(query);
       return vectorStoreService.searchSimilar(embedding, 3);
   }
}
