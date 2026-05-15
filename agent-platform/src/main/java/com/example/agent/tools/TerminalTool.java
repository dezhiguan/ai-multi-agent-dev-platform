package com.example.agent.tools;

import org.springframework.stereotype.Component;


/**
 * Shell 命令执行工具
 */
@Component
public class TerminalTool {

    /**
     * 执行命令并返回输出
     */
    public String exec(String command) throws Exception {
        Process process = new ProcessBuilder(shellCommand(command))
                .redirectErrorStream(true)
                .start();

        String output = new
                String(process.getInputStream().readAllBytes());
        int exitCode = process.waitFor();

        process.destroy();

        return "exitCode=" + exitCode + "\n" + output;
    }

    private String[] shellCommand(String command) {
        if (isWindows()) {
            return new String[]{"cmd.exe", "/c", command};
        }
        return new String[]{"/bin/sh", "-c", command};
    }

    private boolean isWindows() {
        return
                System.getProperty("os.name").toLowerCase().contains("win");
    }

}