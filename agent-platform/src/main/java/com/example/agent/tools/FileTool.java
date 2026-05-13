package com.example.agent.tools;

import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 文件操作工具：Agent 用来生成代码文件
 */
@Component
public class FileTool {

    /**
     * 创建目录（如果不存在）
     */
    public void createDir(String dirPath) throws IOException {
        Files.createDirectories(Paths.get(dirPath));
    }

    /**
     * 写入文件（覆盖）
     */
    public void write(String filePath, String content) throws IOException {
        createDir(Paths.get(filePath).getParent().toString());
        Files.write(Paths.get(filePath), content.getBytes());
    }

    /**
     * 读取文件
     */
    public String read(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }
}