package com.example.agent.tools;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
    public String test(String projectDir) throws Exception {
        String cmd = "cd " + projectDir + " && mvn clean test";
        return terminalTool.exec(cmd);
    }

    /**
     * 执行 mvn clean package
     */
    public String packageProj(String projectDir) throws Exception {
        String cmd = "cd " + projectDir + " && mvn clean package";
        return terminalTool.exec(cmd);
    }
}