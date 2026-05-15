package com.example.agent.tools;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Maven 工具：编译、测试
 */
@Component
@RequiredArgsConstructor
public class MavenTool {

    private final TerminalTool terminalTool;

    /**
     * 执行 mvn clean test
     */
    public String test(String projectPath) throws Exception {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    isWindows() ? "mvn.cmd" : "mvn",
                    "test"
            );
            pb.directory(new File(projectPath));
            pb.redirectErrorStream(true);
            Process process = pb.start();
            process.waitFor();

            return "测试执行完成，退出码：" + process.exitValue();
        } catch (Exception e) {
            return "测试失败：" + e.getMessage();
        }
    }

    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    /**
     * 执行 mvn clean package
     */
    public String packageProj(String projectDir) throws Exception {
        String cmd = "cd " + projectDir + " && mvn clean package";
        return terminalTool.exec(cmd);
    }
}