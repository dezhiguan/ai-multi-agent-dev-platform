package com.example.agent.tools;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 前端 npm 工具
 */
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

    /**
     * 后台启动 Vite 前端服务
     */
    public String devBackground(String webDir) throws Exception {
        String cmd = "cd " + webDir + " && nohup npm run dev > frontend.log 2>&1 &";
        return terminalTool.exec(cmd);
    }
}