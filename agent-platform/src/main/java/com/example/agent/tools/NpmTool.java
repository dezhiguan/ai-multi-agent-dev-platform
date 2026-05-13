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
     * npm run dev
     */
    public String dev(String webDir) throws Exception {
        String cmd = "cd " + webDir + " && npm run dev";
        return terminalTool.exec(cmd);
    }
}