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
    public String test(String projectPath) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    isWindows() ? "mvn.cmd" : "mvn",
                    "test"
            );
            pb.directory(new File(projectPath));
            pb.redirectErrorStream(true);

            Process process = pb.start();

            String output = new
                    String(process.getInputStream().readAllBytes());
            int exitCode = process.waitFor();

            return """
                  exitCode=%d
                  %s
                  """.formatted(exitCode, output);
        } catch (Exception e) {
            return "exitCode=-1\n测试执行异常：" + e.getMessage();
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

    /**
     * 后台启动 Spring Boot 服务
     */
    public String springBootRun(String projectDir) throws
            Exception {
        String cmd = "cd " + projectDir + " && nohup mvn spring-boot:run > backend.log 2>&1 &";
        return terminalTool.exec(cmd);
    }


}