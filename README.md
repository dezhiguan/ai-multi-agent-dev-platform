# AI Multi-Agent Dev Platform

一个基于 Java/Spring Boot 的多智能体软件研发平台原型。项目目标是把“需求分析、知识库检索、代码生成、测试生成、自动修复、构建运行”串成一条可执行的 AI 软件工程流水线，并以订单管理系统作为端到端生成案例。

## 项目定位

本项目不是一个普通订单系统，而是一个面向 Java 企业研发场景的 AI Agent 编排平台。平台通过多个职责清晰的 Agent 协作，将自然语言需求转化为可运行的前后端项目。

当前示例任务：

```text
开发订单管理系统，包含创建、列表、详情
```

平台会生成：

```text
business-workspace/order-service  # Spring Boot 后端
business-workspace/order-web      # React + Vite 前端
```

## 核心能力

- PRD 分析：将原始需求转换为结构化需求信息。
- RAG 检索：从本地知识库中检索代码规范、接口规范、业务资料。
- 后端代码生成：生成 Spring Boot Controller、Service、Repository、Entity、DTO 等代码。
- 前端代码生成：生成 React 页面、表单、表格、API 请求封装和 Vite 工程骨架。
- 测试生成与执行：生成后端单元测试，并通过 Maven 执行测试。
- 自动修复：测试失败时，将错误日志和代码上下文交给修复 Agent 生成补丁。
- 运行托管：构建前后端项目，启动服务并做 HTTP 健康检查。

## 技术栈

- Java 17
- Spring Boot 3.2
- Spring MVC
- Spring Data JPA
- H2 Database
- Maven
- React
- TypeScript
- Vite
- DeepSeek API
- 本地 RAG 文档解析与向量检索雏形

## 关键入口

启动 Agent 平台：

```bash
cd agent-platform
mvn spring-boot:run
```

触发工作流：

```bash
curl http://localhost:8080/workflow/start/demo001
```

生成后端默认地址：

```text
http://localhost:8090
```

生成前端默认地址：

```text
http://localhost:5173
```

## 工作流概览

```text
WorkflowController
  -> AgentSupervisor
      -> PrdAgent
      -> RagAgent
      -> BackendCodeAgent
      -> FrontendCodeAgent
      -> TestAgent
      -> CodeFixAgent
      -> RuntimeAgent
```

## 当前状态

已完成：

- 多 Agent 固定流程编排。
- DeepSeek 调用封装。
- Prompt 模板化管理。
- 后端代码生成和基础校验。
- 前端代码生成和基础校验。
- Maven 测试执行。
- 测试失败后的自动修复链路。
- 前后端构建与运行链路。
- 订单管理系统端到端生成案例。

需要继续完善：

- 工作流异步化和任务进度查询。
- 运行日志实时查看和前端可视化进度页面。
- 工作流状态持久化、失败恢复和断点续跑。
- Agent 执行记录、Prompt 版本、模型返回记录落库。
- 更安全的文件写入和命令执行沙箱。
- 更通用的字段契约校验，避免前端下划线字段和后端驼峰字段不一致。
- 多业务类型支持，而不是只围绕订单管理系统。

## 文档

- [项目总结说明](./PROJECT_SUMMARY.md)
- [简历写法](./RESUME_GUIDE.md)
- [面试题与回答话术](./INTERVIEW_QA.md)

## 项目价值判断

如果作为真实企业项目，本项目的价值在于：用 Java 技术栈实现一个可控的 Agent 编排和软件研发自动化平台，覆盖从需求到运行的闭环，适合展示 Java 后端能力、AI Agent 编排理解、工程化落地意识和问题排查能力。

但它当前更接近企业级原型，还不是生产级 Agent 平台。生产化需要继续补齐任务异步化、状态持久化、权限、安全沙箱、日志追踪、人工审批、成本控制、评测体系和可观测性。

## 与主流 Agent 框架的关系

本项目不是要替代 LangGraph、Microsoft Agent Framework、CrewAI 这类成熟框架，而是用 Java/Spring Boot 亲手实现一条可解释、可调试、贴近企业 Java 研发流程的 Agent 软件工程流水线。

参考：

- LangGraph 官方文档强调 durable execution、human-in-the-loop、memory、streaming 等能力。
- Microsoft Agent Framework 强调状态管理、类型安全、中间件、遥测和显式多 Agent 工作流。
- CrewAI 强调 Agents、Tasks、Flows、guardrails、memory、observability 和企业部署能力。
