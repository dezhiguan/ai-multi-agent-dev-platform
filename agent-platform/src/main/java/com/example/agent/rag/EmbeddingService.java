package com.example.agent.rag;

import org.springframework.stereotype.Service;

@Service
public class EmbeddingService {

    //生成向量（后面对接 OpenAI Embedding）
  public float[]  createEmbedding(String text){
      return new float[1536]; // 占位向量
    }
}
