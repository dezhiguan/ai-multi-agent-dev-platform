package com.example.agent.agents;

import com.example.agent.state.AgentState;
import com.example.agent.state.RuntimeResult;
import com.example.agent.tools.MavenTool;
import com.example.agent.tools.NpmTool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RuntimeAgent implements BaseAgent {

    private final MavenTool mavenTool;
    private final NpmTool npmTool;

    @Override
    public AgentState execute(AgentState state) {
        System.out.println("=== RuntimeAgent 开始启动前后端服务 ===");

        try {
            // 1. 模拟启动后端
            String backendDir = "business-workspace/order-service";
            mavenTool.packageProj(backendDir);

            // 2. 模拟启动前端
            String frontendDir = "business-workspace/order-web";
            npmTool.install(frontendDir);

            // 3. 回填运行结果
            RuntimeResult result = state.getRuntimeResult();
            result.setSuccess(true);
            result.setBackendUrl("http://localhost:8090");
            result.setFrontendUrl("http://localhost:5173");

            System.out.println("=== 服务启动完成 ===");
            System.out.println("后端地址：" + result.getBackendUrl());
            System.out.println("前端地址：" + result.getFrontendUrl());

        } catch (Exception e) {
            state.addError("服务启动失败：" + e.getMessage());
            e.printStackTrace();
        }
        return state;
    }

    @Override
    public String getAgentName() {
        return "RuntimeAgent";
    }
}