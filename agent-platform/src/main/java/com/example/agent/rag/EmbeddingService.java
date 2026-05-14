package com.example.agent.rag;

import com.huaban.analysis.jieba.JiebaSegmenter;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class EmbeddingService {

    private final JiebaSegmenter segmenter = new JiebaSegmenter();
    private static final int VECTOR_SIZE = 1536;

    // 真实：中文分词 → 生成数值向量
    public float[] createEmbedding(String text) {
        List<String> words = segmenter.sentenceProcess(text);
        float[] vector = new float[VECTOR_SIZE];
        Arrays.fill(vector, 0.0f);

        for (int i = 0; i < words.size() && i < VECTOR_SIZE; i++) {
            vector[i] = words.get(i).hashCode() % 100 / 100.0f;
        }
        return vector;
    }

    // 余弦相似度计算
    public float similarity(float[] v1, float[] v2) {
        float dot = 0, a = 0, b = 0;
        for (int i = 0; i < v1.length; i++) {
            dot += v1[i] * v2[i];
            a += v1[i] * v1[i];
            b += v2[i] * v2[i];
        }
        return (float) (dot / Math.sqrt(a * b));
    }
}
