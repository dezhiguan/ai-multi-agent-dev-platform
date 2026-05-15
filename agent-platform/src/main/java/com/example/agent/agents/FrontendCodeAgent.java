package com.example.agent.agents;

import com.example.agent.llm.LlmClient;
import com.example.agent.state.AgentState;
import com.example.agent.tools.FileTool;
import com.example.agent.tools.PromptUtil;
import com.example.agent.util.JsonCleanUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

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
        System.out.println("=== FrontendCodeAgent 开始真实生成前端代码 ===");

        try {
            String prdInfo = objectMapper.writeValueAsString(state.getPrdAnalysis());
            String prompt = PromptUtil.frontendPrompt(prdInfo);

            String raw = llmClient.chat(prompt);
            String json = JsonCleanUtil.cleanJson(raw);
            Map<String, String> codeMap = objectMapper.readValue(json, Map.class);
            validateGeneratedCode(codeMap);

            String base = "business-workspace/order-web/src/";
            for (Map.Entry<String, String> entry : codeMap.entrySet()) {
                fileTool.write(base + entry.getKey(), entry.getValue());
                System.out.println("写入前端：" + entry.getKey());
            }

            writeFrontendScaffold();

            state.getFrontendCodeResult().setSuccess(true);
            System.out.println("=== 前端代码全部生成完成 ===");
        } catch (Exception e) {
            e.printStackTrace();
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
                "    \"react-dom\": \"latest\"",
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
                "    \"moduleResolution\": \"Node\",",
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
                "    \"moduleResolution\": \"Node\",",
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

    @Override
    public String getAgentName() {
        return "FrontendCodeAgent";
    }
}
