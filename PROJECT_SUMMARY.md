# 项目总结说明

## 1. 项目一句话介绍

AI Multi-Agent Dev Platform 是一个基于 Java/Spring Boot 的多智能体软件研发平台原型。它通过多个专职 Agent 协作，将自然语言需求自动转化为可运行的前后端业务系统，并完成测试生成、自动修复、构建启动和健康检查。

## 2. 项目背景

企业内部研发中，大量 CRUD、后台管理、接口联调、单元测试和前后端脚手架工作存在重复性。大模型可以生成代码，但直接使用大模型有几个问题：

- 输出不稳定，格式不一定满足工程约束。
- 缺少完整流程编排，无法覆盖从需求到运行的闭环。
- 缺少企业知识库约束，容易生成不符合团队规范的代码。
- 缺少测试、修复、运行、追踪等工程化能力。

本项目尝试用多 Agent 拆分软件研发流程，让每个 Agent 只负责一个明确职责，再由 Java 后端统一编排执行。

## 3. 项目架构

```text
ai-multi-agent-dev-platform
├── agent-platform
│   ├── controller      # HTTP 入口
│   ├── workflow        # 工作流编排
│   ├── agents          # 多 Agent 执行器
│   ├── llm             # DeepSeek 调用封装
│   ├── rag             # 知识库解析、切片、检索
│   ├── tools           # 文件、Maven、Npm、终端工具
│   ├── state           # Agent 共享状态对象
│   └── resources
│       ├── prompts     # Prompt 模板
│       └── db/migration
├── business-workspace
│   ├── order-service   # 生成的后端项目
│   └── order-web       # 生成的前端项目
└── knowledge-base      # 本地知识库目录
```

## 4. 核心流程

工作流入口：

```text
GET /workflow/start/{taskId}
```

核心编排类：

```text
AgentSupervisor
```

执行链路：

```text
1. PrdAgent
   读取原始需求，调用 LLM 生成结构化 PRD。

2. RagAgent
   根据 PRD 查询关键词检索知识库，返回规范或参考资料。

3. BackendCodeAgent
   基于 PRD 和 RAG 上下文生成 Spring Boot 后端代码。

4. FrontendCodeAgent
   基于 PRD 生成 React + TypeScript 前端代码。

5. TestAgent
   读取后端核心代码，生成 Controller 测试并执行 Maven test。

6. CodeFixAgent
   如果测试失败，读取错误日志和后端代码，调用 LLM 生成修复代码。

7. RuntimeAgent
   测试通过后，打包后端、安装前端依赖、构建前端、启动前后端服务并健康检查。
```

## 5. 重点模块

### 5.1 AgentSupervisor

职责：工作流总调度器。

价值：

- 控制 Agent 执行顺序。
- 维护 `WorkflowContext`。
- 记录当前步骤和完成步骤。
- 发现错误后中断后续执行。
- 支持测试失败后的自动修复重试。

### 5.2 AgentState

职责：所有 Agent 的共享上下文。

保存内容：

- taskId
- 原始 PRD
- 结构化 PRD 分析结果
- RAG 检索结果
- 后端生成结果
- 前端生成结果
- 测试执行结果
- 运行结果
- 错误列表

### 5.3 LlmClient

职责：统一封装 DeepSeek API。

价值：

- 屏蔽模型 API 细节。
- 所有 Agent 通过同一个客户端调用模型。
- 后续可扩展模型路由、重试、超时、成本统计和审计日志。

### 5.4 PromptUtil 与 prompts

职责：Prompt 模板加载和变量替换。

价值：

- Prompt 和 Java 逻辑分离。
- 便于独立调优不同 Agent 的提示词。
- 便于做 Prompt 版本管理和 A/B 测试。

### 5.5 BackendCodeAgent

职责：生成后端代码。

当前生成内容：

- `Order`
- `OrderRepository`
- `OrderCreateRequest`
- `OrderDTO`
- `OrderService`
- `OrderServiceImpl`
- `OrderController`
- `OrderServiceApplication`
- `pom.xml`
- `application.yml`

已有保护：

- 校验生成文件是否完整。
- 校验包名是否为 `com.example.order`。
- 拦截 Markdown 代码块。
- 拦截错误包名。

### 5.6 FrontendCodeAgent

职责：生成前端代码。

当前生成内容：

- `OrderPage.tsx`
- `OrderForm.tsx`
- `OrderTable.tsx`
- `orderApi.ts`
- `order.ts`
- Vite 工程配置

需要重点完善：

- 前后端字段契约自动对齐。
- 不应硬编码订单字段，应从后端 DTO 自动推导接口字段。
- 对生成代码做 TypeScript 编译前置校验。

### 5.7 TestAgent

职责：生成并执行后端测试。

价值：

- 让代码生成不是停留在“能看”，而是进入“能测”。
- 为 CodeFixAgent 提供错误日志输入。

### 5.8 CodeFixAgent

职责：自动修复测试失败代码。

价值：

- 形成“生成 -> 测试 -> 修复 -> 再测试”的闭环。
- 体现 Agent 不是一次性生成，而是能根据反馈迭代。

### 5.9 RuntimeAgent

职责：运行生成项目。

当前流程：

- `mvn clean package`
- `npm install`
- `npm run build`
- 启动后端
- 检查 `http://localhost:8090/api/orders`
- 启动前端
- 检查 `http://localhost:5173`

需要重点完善：

- 后台启动命令不能阻塞主线程。
- 需要日志实时追踪。
- 需要任务进度接口。
- 需要端口占用检测和进程管理。

## 6. 已完成的功能

- Spring Boot 平台基础工程。
- DeepSeek API 调用封装。
- PRD Agent。
- RAG Agent 雏形。
- 后端代码生成 Agent。
- 前端代码生成 Agent。
- 测试生成和执行 Agent。
- 自动修复 Agent。
- 运行 Agent。
- Prompt 模板化。
- 文件读写工具。
- Maven/Npm/Terminal 工具。
- 订单管理系统生成案例。
- 后端测试通过。
- 前端构建通过。
- 前后端运行链路基本打通。

## 7. 当前主要问题

### 7.1 工作流不可观察

问题：

- `/workflow/start/{taskId}` 是同步接口。
- 页面或 curl 只能等最终结果。
- 中间步骤、耗时、日志、错误不可视化。

改进：

- 工作流改为异步任务。
- 增加 `/workflow/status/{taskId}`。
- 增加 `/workflow/logs/{taskId}`。
- 前端页面实时展示每个 Agent 的状态。

### 7.2 服务启动命令可能阻塞

问题：

- `mvn spring-boot:run` 和 `npm run dev` 是长期运行进程。
- 如果用 `waitFor()` 等待，会导致 RuntimeAgent 卡住。

改进：

- 使用 `ProcessBuilder.start()` 后立即返回 pid。
- 将日志重定向到 `backend.log` 和 `frontend.log`。
- 健康检查独立执行。

### 7.3 前后端字段契约不稳定

问题：

- 后端 Jackson 默认输出驼峰字段。
- 前端模型可能生成下划线字段。
- 会导致 `productId` 等字段为 null，引发 JPA 非空约束异常。

改进：

- 从后端 DTO 自动解析字段。
- 生成前端 Prompt 时注入字段契约。
- 校验前端代码不能出现错误字段。
- 后续可引入 OpenAPI 作为统一契约。

### 7.4 缺少持久化和断点续跑

问题：

- 任务状态主要保存在内存。
- 服务重启后任务不可恢复。
- Agent 执行历史不可查询。

改进：

- 建立任务表、步骤表、模型调用表、日志表。
- 每个步骤开始、完成、失败都落库。
- 支持失败后从指定步骤重跑。

### 7.5 安全边界不足

问题：

- Agent 可以写文件、执行 Maven/Npm 命令。
- 生产环境必须限制路径、命令、网络和资源。

改进：

- 文件写入白名单。
- 命令执行白名单。
- Docker 沙箱运行生成项目。
- 每个任务独立 workspace。

## 8. 如果作为真实企业项目，有价值吗

有价值，但要定位准确。

这个项目当前最有价值的点不是“生成了一个订单系统”，而是实现了一个 Java 技术栈下的 AI 软件工程自动化闭环：

```text
需求 -> RAG -> 代码生成 -> 测试 -> 修复 -> 运行
```

对于企业来说，它可以落在这些场景：

- 内部 CRUD 后台快速生成。
- 统一代码规范下的脚手架生成。
- 老项目接口测试补全。
- 低风险模块的自动化开发辅助。
- Java 团队内部 AI Agent 平台探索。

但它目前还不能直接等同于生产级平台，因为缺少任务系统、权限、安全沙箱、审计、评测、成本控制和稳定性保障。

## 9. 与主流智能体框架对比

### 9.1 对比 LangGraph

LangGraph 更成熟，官方强调 durable execution、human-in-the-loop、memory、streaming 和可恢复长任务。

本项目优势：

- Java/Spring Boot 原生，更贴近 Java 企业后端团队。
- 面向软件研发全流程，而不是通用 Agent 对话流。
- 文件生成、Maven、Npm、Spring Boot 运行链路更贴近 Java 工程实践。
- 适合展示你对底层编排、状态、工具调用和工程落地的理解。

本项目劣势：

- 没有成熟的图编排能力。
- 没有内建 checkpoint 和断点恢复。
- 没有成熟 streaming。
- 没有完善的 human-in-the-loop。

### 9.2 对比 Microsoft Agent Framework / AutoGen

Microsoft Agent Framework 是 AutoGen 和 Semantic Kernel 的后继方向，强调企业能力、状态管理、类型安全、中间件、遥测和显式工作流。

本项目优势：

- 实现简单、链路透明，适合理解多 Agent 内核。
- Java 技术栈明确，方便和企业内部 Java 系统整合。
- 目标场景聚焦在代码生成和研发自动化。

本项目劣势：

- Agent 抽象不够通用。
- 缺少中间件、遥测、状态管理和插件生态。
- 缺少多 Agent 并发、handoff、group chat 等高级编排模式。

### 9.3 对比 CrewAI

CrewAI 强调 Agent、Task、Flow、guardrails、memory、observability 和企业部署平台。

本项目优势：

- Java 后端开发者更容易解释和维护。
- 与 Maven、Spring Boot、企业后端工程结合更自然。
- 更适合作为 Java Agent 开发岗位的作品。

本项目劣势：

- 没有成熟的 visual builder、企业部署、监控和工具市场。
- Prompt、Agent、Task 的抽象层次还比较初级。
- 缺少丰富的第三方工具和 SaaS 集成。

## 10. 有必要自己开发吗

如果目标是生产效率，没必要完全从零重复造一个成熟框架。

如果目标是应聘 Java Agent 开发、理解 Agent 工程化、展示架构能力，这个项目非常值得自己开发。

更现实的路线是：

```text
自己实现核心编排和 Java 工程集成
借鉴成熟框架的设计思想
后续按企业级要求补齐可观测性、状态持久化、安全和评测
```

面试时建议这样表达：

```text
我没有把项目定位为替代 LangGraph 或 CrewAI，而是做一个面向 Java 企业研发流程的 Agent 平台原型。我的重点是把 Agent 编排、RAG、代码生成、测试修复、构建运行这些链路用 Java 工程化方式打通。成熟框架有很强的通用能力，但企业内部经常需要和 Java 项目、Maven、Spring Boot、权限、审计、私有知识库深度集成，这部分是我自己实现的价值。
```

## 11. 接下来开发计划

### Day 1：工作流异步化

目标：

- `/workflow/start/{taskId}` 立即返回。
- 后台线程执行工作流。
- 前端或 curl 可以查询任务状态。

任务：

- 新增 `WorkflowTaskService`。
- 使用 `ExecutorService` 或 Spring `@Async`。
- `WorkflowContext` 按 taskId 缓存。
- 新增 `GET /workflow/status/{taskId}`。

验收：

- 启动任务接口 1 秒内返回。
- 能查询当前步骤和错误信息。

### Day 2：步骤进度和日志落库

目标：

- 每个 Agent 的开始、结束、耗时、错误都能查询。

任务：

- 设计 `agent_task` 表。
- 设计 `agent_step_log` 表。
- 在 `executeAndRecord` 中落库。
- 返回步骤列表。

验收：

- 查询任务能看到每一步状态。
- 服务重启后历史任务仍可查询。

### Day 3：RuntimeAgent 可观测性

目标：

- 清楚知道后端/前端启动进度。
- 能查看 `backend.log` 和 `frontend.log`。

任务：

- `MavenTool.springBootRun` 改为非阻塞启动。
- `NpmTool.devBackground` 改为非阻塞启动。
- 记录进程 pid。
- 增加日志读取接口。
- 增加端口占用检查。

验收：

- RuntimeAgent 不再卡在启动命令。
- 能查询后端和前端日志。

### Day 4：前后端契约自动对齐

目标：

- 彻底解决驼峰和下划线字段不一致问题。

任务：

- 解析 `OrderDTO.java` 和 `OrderCreateRequest.java` 字段。
- 生成字段契约文本。
- 注入 `FrontendCodeAgent` prompt。
- 校验前端代码不能出现错误字段。

验收：

- 前端生成字段和后端 DTO 一致。
- 创建订单不再出现 `productId` 为 null。

### Day 5：OpenAPI 契约生成

目标：

- 用 OpenAPI 作为前后端共同契约。

任务：

- 后端生成或维护 OpenAPI JSON。
- 前端根据 OpenAPI 生成类型和 API client。
- FrontendCodeAgent 只生成页面，不手写接口字段。

验收：

- 类型从接口契约生成。
- 字段错配概率显著降低。

### Day 6：Prompt 版本管理

目标：

- 每次模型调用可追溯。

任务：

- 记录 promptName、promptVersion、input、output。
- Prompt 文件增加版本号。
- 失败时能定位是哪次模型输出问题。

验收：

- 任意任务可查看模型输入输出。

### Day 7：CodeFixAgent 增强

目标：

- 自动修复更可控。

任务：

- 让修复 Agent 只返回补丁文件列表。
- 校验修复文件白名单。
- 增加修复前后 diff。
- 增加最大重试和失败原因分类。

验收：

- 修复过程可追踪、可审计。

### Day 8：安全沙箱

目标：

- 限制 Agent 对本机的破坏能力。

任务：

- 文件写入限制在 `business-workspace/{taskId}`。
- 命令执行白名单。
- 禁止删除根目录和用户目录。
- 后续引入 Docker 执行生成项目。

验收：

- 非法路径写入会被拒绝。
- 非白名单命令会被拒绝。

### Day 9：任务隔离

目标：

- 多个任务互不覆盖。

任务：

- 每个 taskId 一个 workspace。
- 生成项目路径改为 `business-workspace/tasks/{taskId}`。
- RuntimeAgent 动态分配端口。

验收：

- 两个任务可以并行生成，不互相覆盖。

### Day 10：RAG 增强

目标：

- 让企业规范真正影响生成结果。

任务：

- 完善文档解析、切片、embedding。
- 增加知识库上传接口。
- 增加检索结果评分。
- Prompt 中注入高相关规范。

验收：

- 修改知识库规范后，生成代码风格随之变化。

### Day 11：前端管理台

目标：

- 提供可视化操作界面。

任务：

- 任务列表。
- 任务详情。
- 步骤进度条。
- 日志查看。
- 生成文件预览。

验收：

- 不依赖命令行也能观察工作流。

### Day 12：人工审批 Human-in-the-loop

目标：

- 关键节点可人工确认。

任务：

- 生成代码后暂停。
- 用户查看 diff。
- 用户选择继续、重试或修改需求。

验收：

- 工作流可以暂停并恢复。

### Day 13：评测体系

目标：

- 衡量 Agent 生成质量。

任务：

- 定义编译通过率、测试通过率、修复成功率、耗时、token 成本。
- 存储每次任务指标。
- 增加对比报告。

验收：

- 能量化 Prompt 调优效果。

### Day 14：简历级收尾

目标：

- 项目达到可展示状态。

任务：

- README 完善。
- 架构图补充。
- 演示脚本补充。
- 准备 3 分钟项目介绍。
- 准备常见面试题回答。

验收：

- 能从零演示一次需求到系统运行。
- 能解释每个 Agent 的设计取舍。

## 12. 参考资料

- LangGraph overview: https://docs.langchain.com/langgraph
- LangGraph durable execution: https://docs.langchain.com/oss/python/langgraph/durable-execution
- Microsoft Agent Framework overview: https://learn.microsoft.com/en-us/agent-framework/overview/
- CrewAI docs: https://docs.crewai.com/
