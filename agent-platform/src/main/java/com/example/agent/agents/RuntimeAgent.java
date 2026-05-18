package com.example.agent.agents;

import com.example.agent.state.AgentState;
import com.example.agent.state.RuntimeResult;
import com.example.agent.tools.MavenTool;
import com.example.agent.tools.NpmTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URI;

@Slf4j
@Component
@RequiredArgsConstructor
public class RuntimeAgent implements BaseAgent {

    private final MavenTool mavenTool;
    private final NpmTool npmTool;

    @Override
    public AgentState execute(AgentState state) {
        log.info("[RuntimeAgent] 开始启动前后端服务");
        try {
            if (state.getTestResult() == null || !state.getTestResult().isPass()) {
                state.addError("测试未通过，禁止启动服务");
                return state;
            }

            // 1. 打包后端
            String backendDir = "business-workspace/order-service";
            log.info("[RuntimeAgent] 开始后端打包，目录={}", backendDir);
            String backendPackageResult = mavenTool.packageProj(backendDir);
            if (!backendPackageResult.startsWith("exitCode=0")) {
                log.error("[RuntimeAgent] 后端打包失败：\n{}", backendPackageResult);
                state.addError("后端打包失败：\n" + backendPackageResult);
                return state;
            }
            log.info("[RuntimeAgent] 后端打包成功");

            // 2. 安装前端依赖
            String frontendDir = "business-workspace/order-web";
            log.info("[RuntimeAgent] 开始安装前端依赖，目录={}", frontendDir);
            String frontendInstallResult = npmTool.install(frontendDir);
            if (!frontendInstallResult.startsWith("exitCode=0")) {
                log.error("[RuntimeAgent] 前端依赖安装失败：\n{}", frontendInstallResult);
                state.addError("前端依赖安装失败：\n" + frontendInstallResult);
                return state;
            }
            log.info("[RuntimeAgent] 前端依赖安装成功");

            // 3. 构建前端
            log.info("[RuntimeAgent] 开始构建前端");
            String frontendBuildResult = npmTool.build(frontendDir);
            if (!frontendBuildResult.startsWith("exitCode=0")) {
                log.error("[RuntimeAgent] 前端构建失败：\n{}", frontendBuildResult);
                state.addError("前端构建失败：\n" + frontendBuildResult);
                return state;
            }
            log.info("[RuntimeAgent] 前端构建成功");

            log.info("[RuntimeAgent] 开始启动后端服务");

            long backendStartBegin = System.currentTimeMillis();
            log.info("[RuntimeAgent] 准备调用 mavenTool.springBootRun，目录={}", backendDir);

                    String backendStartResult = mavenTool.springBootRun(backendDir);

            log.info("[RuntimeAgent] mavenTool.springBootRun 已返回，耗时={} ms，结果={}",
                    System.currentTimeMillis() - backendStartBegin,
                    backendStartResult);

            if (!backendStartResult.startsWith("exitCode=0")) {
                log.error("[RuntimeAgent] 后端服务启动命令执行失败：\n{}",
                        backendStartResult);
                state.addError("后端服务启动命令执行失败：\n" +
                        backendStartResult);
                return state;
            }
            log.info("[RuntimeAgent] 后端启动命令执行成功，开始健康检查");

            log.info("[RuntimeAgent] 检查后端服务：http://localhost:8090/api/orders");
            // 5. 检查后端服务是否可访问
            if (!waitForHttp("http://localhost:8090/api/orders", 30)) {
                state.addError("后端服务启动后不可访问：http://localhost:8090/api/orders ");
                return state;
            }

            log.info("[RuntimeAgent] 开始启动前端服务");

            log.info("[RuntimeAgent] 开始启动前端服务");

            long frontendStartBegin = System.currentTimeMillis();
            log.info("[RuntimeAgent] 准备调用 npmTool.devBackground，目录={}",
                    frontendDir);

            // 6. 启动前端服务
            String frontendStartResult = npmTool.devBackground(frontendDir);

            log.info("[RuntimeAgent] npmTool.devBackground 已返回，耗时={}ms，结果={}",
                    System.currentTimeMillis() - frontendStartBegin,
                    frontendStartResult);

            if (!frontendStartResult.startsWith("exitCode=0")) {
                log.error("[RuntimeAgent] 前端服务启动命令执行失败：\n{}",
                        frontendStartResult);
                state.addError("前端服务启动命令执行失败：\n" +
                        frontendStartResult);
                return state;
            }

            // 7. 检查前端服务是否可访问
            if (!waitForHttp("http://localhost:5173", 30)) {
                state.addError("前端服务启动后不可访问：http://localhost:5173");
                return state;
            }

            // 8. 回填运行结果
            RuntimeResult result = state.getRuntimeResult();
            result.setSuccess(true);
            result.setBackendUrl("http://localhost:8090");
            result.setFrontendUrl("http://localhost:5173");

            log.info("[RuntimeAgent] 服务启动完成，backendUrl={}， frontendUrl={}",
                    result.getBackendUrl(),
                    result.getFrontendUrl());

        } catch (Exception e) {
            state.addError("服务启动失败：" + e.getMessage());
            log.error("[RuntimeAgent] 服务启动失败", e);
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
