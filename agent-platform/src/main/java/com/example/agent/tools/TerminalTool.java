package com.example.agent.tools;

import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Shell 命令执行工具
 */
@Component
public class TerminalTool {

    /**
     * 执行命令并返回输出
     */
    public String exec(String command) throws Exception {
        Process process = Runtime.getRuntime().exec(command);

        // 读取输出
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }

        // 等待执行完成
        process.waitFor();
        reader.close();
        process.destroy();

        return sb.toString();
    }
}