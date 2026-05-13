package com.example.agent.rag;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentChunkService {

    // 简单按长度分块（可优化）
    public List<String> chunk(String text, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        int index = 0;
        while (index < text.length()) {
            int end = Math.min(index + chunkSize, text.length());
            chunks.add(text.substring(index, end));
            index += chunkSize;
        }
        return chunks;
    }
}