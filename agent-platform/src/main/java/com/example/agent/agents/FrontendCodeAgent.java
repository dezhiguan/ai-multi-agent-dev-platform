package com.example.agent.agents;

import com.example.agent.llm.LlmClient;
import com.example.agent.state.AgentState;
import com.example.agent.tools.FileTool;
import com.example.agent.tools.PromptUtil;
import com.example.agent.util.JsonCleanUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Component
@RequiredArgsConstructor
public class FrontendCodeAgent implements BaseAgent {

    private final LlmClient llmClient;
    private final FileTool fileTool;
    private final ObjectMapper objectMapper;

    private static final Set<String> REQUIRED_FILES = Set.of(
            "pages/OrderPage.tsx",
            "components/OrderForm.tsx",
            "components/OrderTable.tsx",
            "api/orderApi.ts",
            "types/order.ts"
    );

    @Override
    public AgentState execute(AgentState state) {
        log.info("[FrontendCodeAgent] 开始生成前端代码");

        try {
            long start = System.currentTimeMillis();

            log.info("[FrontendCodeAgent] 开始构建提示词");
            String prdInfo = objectMapper.writeValueAsString(state.getPrdAnalysis());
            String prompt = PromptUtil.frontendPrompt(prdInfo);
            log.info("[FrontendCodeAgent] 提示词构建完成，promptLength={}", prompt.length());

            log.info("[FrontendCodeAgent] 开始调用大模型");
            String raw = llmClient.chat(prompt);
            log.info("[FrontendCodeAgent] 大模型返回完成，耗时={}ms，rawLength={}",
                    System.currentTimeMillis() - start,
                    raw == null ? 0 : raw.length());

            log.info("[FrontendCodeAgent] 开始清洗 JSON");
            String json = JsonCleanUtil.cleanJson(raw);

            log.info("[FrontendCodeAgent] 开始解析 JSON");
            Map<String, String> codeMap = objectMapper.readValue(json, Map.class);
            log.info("[FrontendCodeAgent] JSON 解析完成，files={}", codeMap.keySet());

            log.info("[FrontendCodeAgent] 开始校验前端生成结果");
            validateGeneratedCode(codeMap);

            String base = "business-workspace/order-web/src/";
            for (Map.Entry<String, String> entry : codeMap.entrySet()) {
                fileTool.write(base + entry.getKey(), entry.getValue());
                log.info("[FrontendCodeAgent] 写入前端文件：{}", entry.getKey());
            }

            log.info("[FrontendCodeAgent] 开始写入 Vite 工程骨架");
            writeFrontendScaffold();

            state.getFrontendCodeResult().setSuccess(true);
            log.info("[FrontendCodeAgent] 前端代码生成完成，总耗时={}ms",
                    System.currentTimeMillis() - start);
        } catch (Exception e) {
            log.error("[FrontendCodeAgent] 前端生成失败", e);
            state.addError("前端生成失败：" + e.getMessage());
        }
        return state;
    }

    private void validateGeneratedCode(Map<String, String> codeMap) {
        if (!codeMap.keySet().equals(REQUIRED_FILES)) {
            throw new IllegalArgumentException("前端生成文件不完整或包含非法文件，实际文件：" + codeMap.keySet());
        }

        for (Map.Entry<String, String> entry : codeMap.entrySet()) {
            String fileName = entry.getKey();
            String content = entry.getValue();

            if (content == null || content.isBlank()) {
                throw new IllegalArgumentException(fileName + " 内容为空");
            }

            if (content.contains("```")) {
                throw new IllegalArgumentException(fileName + " 包含 Markdown 代码块");
            }

            for (String forbiddenField : forbiddenSnakeCaseFields()) {
                if (content.contains(forbiddenField)) {
                    throw new IllegalArgumentException(
                            fileName + " 包含错误的下划线字段名：" +
                                    forbiddenField + "，前端必须使用后端驼峰字段"
                    );
                }
            }

            if (fileName.startsWith("/") || fileName.contains("..") || fileName.contains("\\")) {
                throw new IllegalArgumentException(fileName + " 是非法路径");
            }
        }
    }

    private void writeFrontendScaffold() throws Exception {
        fileTool.write("business-workspace/order-web/package.json", String.join("\n",
                "{",
                "  \"scripts\": {",
                "    \"dev\": \"vite --host 0.0.0.0 --port 5173\",",
                "    \"build\": \"tsc && vite build\",",
                "    \"preview\": \"vite preview --host 0.0.0.0 --port 5173\"",
                "  },",
                "  \"dependencies\": {",
                "    \"@vitejs/plugin-react\": \"latest\",",
                "    \"vite\": \"latest\",",
                "    \"typescript\": \"latest\",",
                "    \"react\": \"latest\",",
                "    \"react-dom\": \"latest\",",
                "    \"@types/react\": \"latest\",",
                "    \"@types/react-dom\": \"latest\"",
                "  },",
                "  \"devDependencies\": {}",
                "}",
                ""));

        fileTool.write("business-workspace/order-web/index.html", String.join("\n",
                "<!doctype html>",
                "<html lang=\"zh-CN\">",
                "  <head>",
                "    <meta charset=\"UTF-8\" />",
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />",
                "    <title>订单管理系统</title>",
                "  </head>",
                "  <body>",
                "    <div id=\"root\"></div>",
                "    <script type=\"module\" src=\"/src/main.tsx\"></script>",
                "  </body>",
                "</html>",
                ""));

        fileTool.write("business-workspace/order-web/vite.config.ts", String.join("\n",
                "import { defineConfig } from 'vite';",
                "import react from '@vitejs/plugin-react';",
                "",
                "export default defineConfig({",
                "  plugins: [react()],",
                "  server: {",
                "    port: 5173,",
                "    proxy: {",
                "      '/api': {",
                "        target: 'http://localhost:8090',",
                "        changeOrigin: true",
                "      }",
                "    }",
                "  }",
                "});",
                ""));

        fileTool.write("business-workspace/order-web/tsconfig.json", String.join("\n",
                "{",
                "  \"compilerOptions\": {",
                "    \"target\": \"ES2020\",",
                "    \"useDefineForClassFields\": true,",
                "    \"lib\": [\"DOM\", \"DOM.Iterable\", \"ES2020\"],",
                "    \"allowJs\": false,",
                "    \"skipLibCheck\": true,",
                "    \"esModuleInterop\": true,",
                "    \"allowSyntheticDefaultImports\": true,",
                "    \"strict\": true,",
                "    \"forceConsistentCasingInFileNames\": true,",
                "    \"module\": \"ESNext\",",
                "    \"moduleResolution\": \"Bundler\",",
                "    \"resolveJsonModule\": true,",
                "    \"isolatedModules\": true,",
                "    \"noEmit\": true,",
                "    \"jsx\": \"react-jsx\"",
                "  },",
                "  \"include\": [\"src\"],",
                "  \"references\": [{ \"path\": \"./tsconfig.node.json\" }]",
                "}",
                ""));

        fileTool.write("business-workspace/order-web/tsconfig.node.json", String.join("\n",
                "{",
                "  \"compilerOptions\": {",
                "    \"composite\": true,",
                "    \"module\": \"ESNext\",",
                "    \"moduleResolution\": \"Bundler\",",
                "    \"allowSyntheticDefaultImports\": true",
                "  },",
                "  \"include\": [\"vite.config.ts\"]",
                "}",
                ""));

        fileTool.write("business-workspace/order-web/src/main.tsx", String.join("\n",
                "import React from 'react';",
                "import ReactDOM from 'react-dom/client';",
                "import App from './App';",
                "",
                "ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(",
                "  <React.StrictMode>",
                "    <App />",
                "  </React.StrictMode>",
                ");",
                ""));

        fileTool.write("business-workspace/order-web/src/App.tsx", String.join("\n",
                "import OrderPage from './pages/OrderPage';",
                "",
                "export default function App() {",
                "  return <OrderPage />;",
                "}",
                ""));
    }

    private String toSnakeCase(String camelCase) {
        return camelCase.replaceAll("([a-z])([A-Z])",
                "$1_$2").toLowerCase();
    }

    private Set<String> forbiddenSnakeCaseFields() {
        return Set.of(
                        "userId",
                        "productId",
                        "totalPrice",
                        "createdAt",
                        "updatedAt"
                ).stream()
                .map(this::toSnakeCase)
                .collect(Collectors.toSet());
    }

    @Override
    public String getAgentName() {
        return "FrontendCodeAgent";
    }
}
