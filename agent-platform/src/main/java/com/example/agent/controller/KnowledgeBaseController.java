package com.example.agent.controller;

import com.example.agent.rag.DocumentChunkService;
import com.example.agent.rag.DocumentParseService;
import com.example.agent.rag.VectorStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/knowledge")
@RequiredArgsConstructor
public class KnowledgeBaseController {

    private final DocumentParseService documentParseService;
    private final DocumentChunkService documentChunkService;
    private final VectorStoreService vectorStoreService;

    /**
     * 上传PDF/Markdown文档到知识库
     */
    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file,
                         @RequestParam("taskId") String taskId) throws Exception {

        //1.转临时文件
        File tempFile = File.createTempFile("temp", null);
        file.transferTo(tempFile);

        //2.解析文本
        String text = documentParseService.parsePdf(tempFile);

        //3.分块
        List<String> chunks = documentChunkService.chunk(text, 3);

        //4.存入向量数据库
        for (String chunk : chunks) {
            vectorStoreService.saveEmbedding(taskId,chunk, new float[1536]);
        }

        tempFile.delete();
        return "上传成功，分块数量：" + chunks.size();
    }

}
