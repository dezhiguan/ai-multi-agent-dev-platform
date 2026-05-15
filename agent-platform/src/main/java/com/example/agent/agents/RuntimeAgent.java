package com.example.agent.agents;

import com.example.agent.state.AgentState;
import com.example.agent.state.RuntimeResult;
import com.example.agent.tools.MavenTool;
import com.example.agent.tools.NpmTool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URI;


@Component
@RequiredArgsConstructor
public class RuntimeAgent implements BaseAgent {

    private final MavenTool mavenTool;
    private final NpmTool npmTool;

    @Override
    public AgentState execute(AgentState state) {
        System.out.println("=== RuntimeAgent 开始启动前后端服务 ===");

        try {
            if (state.getTestResult() == null || !
                    state.getTestResult().isPass()) {
                state.addError("测试未通过，禁止启动服务");
                return state;
            }

            // 1. 打包后端
            String backendDir = "business-workspace/order-service";
            String backendPackageResult = mavenTool.packageProj(backendDir);
            if (!backendPackageResult.startsWith("exitCode=0")) {
                state.addError("后端打包失败：\n" + backendPackageResult);
                return state;
            }

            // 2. 安装前端依赖
            String frontendDir = "business-workspace/order-web";
            String frontendInstallResult = npmTool.install(frontendDir);
            if (!frontendInstallResult.startsWith("exitCode=0")) {
                state.addError("前端依赖安装失败：\n" +
                        frontendInstallResult);
                return state;
            }

            // 3. 构建前端
            String frontendBuildResult = npmTool.build(frontendDir);
            if (!frontendBuildResult.startsWith("exitCode=0")) {
                state.addError("前端构建失败：\n" + frontendBuildResult);
                return state;
            }

            // 4. 启动后端服务
            String backendStartResult =
                    mavenTool.springBootRun(backendDir);
            if (!backendStartResult.startsWith("exitCode=0")) {
                state.addError("后端服务启动命令执行失败：\n" +
                        backendStartResult);
                return state;
            }

            // 5. 检查后端服务是否可访问
            if (!waitForHttp("http://localhost:8090/api/orders", 30)) {
                state.addError("后端服务启动后不可访问：http://localhost:8090 / api / orders ");
                return state;
            }

            // 6. 启动前端服务
            String frontendStartResult =
                    npmTool.devBackground(frontendDir);
            if (!frontendStartResult.startsWith("exitCode=0")) {
                state.addError("前端服务启动命令执行失败：\n" +
                        frontendStartResult);
                return state;
            }

            // 7. 检查前端服务是否可访问
            if (!waitForHttp("http://localhost:5173", 30)) {
                state.addError("前端服务启动后不可访问：http://localhost:5173");
                return state;
            }

            // 7. 回填运行结果
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

    private boolean waitForHttp(String url, int maxRetries) {
        for (int i = 0; i < maxRetries; i++) {
            try {
                HttpURLConnection connection =
                        (HttpURLConnection) URI.create(url).toURL().openConnection();
                connection.setConnectTimeout(1000);
                connection.setReadTimeout(1000);
                connection.setRequestMethod("GET");

                int status = connection.getResponseCode();
                connection.disconnect();

                if (status < 500) {
                    return true;
                }
            } catch (Exception ignored) {
                // 服务可能还在启动，继续重试
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }

        return false;
    }

    @Override
    public String getAgentName() {
        return "RuntimeAgent";
    }
}