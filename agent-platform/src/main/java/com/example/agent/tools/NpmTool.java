package com.example.agent.tools;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 前端 npm 工具
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NpmTool {

    private final TerminalTool terminalTool;

    /**
     * npm install
     */
    public String install(String webDir) throws Exception {
        String cmd = "cd " + webDir + " && npm install";
        return terminalTool.exec(cmd);
    }

    /**
     * npm run build
     */
    public String build(String webDir) throws Exception {
        String cmd = "cd " + webDir + " && npm run build";
        return terminalTool.exec(cmd);
    }

    /**
     * npm run dev
     */
    public String dev(String webDir) throws Exception {
        String cmd = "cd " + webDir + " && npm run dev";
        return terminalTool.exec(cmd);
    }

    public String devBackground(String webDir) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    isWindows() ? "cmd.exe" : "npm",
                    isWindows() ? "/c" : "run",
                    isWindows() ? "npm run dev" : "dev"
            );

            pb.directory(new java.io.File(webDir));
            pb.redirectErrorStream(true);
            pb.redirectOutput(ProcessBuilder.Redirect.appendTo(
                    new java.io.File(webDir, "frontend.log")
            ));

            Process process = pb.start();

            return "exitCode=0\npid=" + process.pid();
        } catch (Exception e) {
            return "exitCode=-1\n前端服务启动异常：" + e.getMessage();
        }
    }

    private boolean isWindows() {
        return
                System.getProperty("os.name").toLowerCase().contains("win");
    }

}