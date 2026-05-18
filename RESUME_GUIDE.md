# 简历写法

## 1. 项目名称

AI 多智能体软件研发平台

英文可写：

AI Multi-Agent Software Development Platform

## 2. 简历项目描述

建议写法：

```text
基于 Java 17、Spring Boot 3、DeepSeek API、RAG、React/Vite 设计并实现 AI 多智能体软件研发平台，支持从自然语言需求出发，自动完成 PRD 分析、知识库检索、前后端代码生成、测试生成、自动修复、构建运行和健康检查。项目以订单管理系统为端到端案例，打通需求到可运行系统的 AI 软件工程闭环。
```

## 3. 技术栈写法

```text
Java 17、Spring Boot 3、Spring MVC、Spring Data JPA、Maven、H2、React、TypeScript、Vite、DeepSeek API、RAG、Prompt Engineering、多 Agent 编排
```

## 4. 项目职责写法

如果你是个人项目，可以写：

```text
个人独立设计并实现平台核心链路，包括多 Agent 工作流编排、LLM 调用封装、Prompt 模板管理、RAG 检索雏形、代码生成、测试执行、自动修复和运行托管模块。
```

如果你想更像企业项目，可以写：

```text
负责 Agent 工作流编排、LLM 调用封装、代码生成链路和运行托管模块设计，实现需求分析、代码生成、测试修复、构建运行的自动化闭环。
```

## 5. 简历项目亮点

可以写 4 到 6 条：

```text
- 设计多 Agent 固定编排流程，将 PRD 分析、RAG 检索、代码生成、测试生成、自动修复、运行托管拆分为独立 Agent，降低单一大模型调用的不确定性。
- 基于 AgentState 设计共享上下文，在不同 Agent 之间传递 PRD、RAG、生成结果、测试报告和运行结果，实现端到端状态流转。
- 封装 DeepSeek API 调用和 Prompt 模板加载机制，将模型调用逻辑与业务编排解耦，便于后续扩展模型路由、Prompt 版本管理和审计。
- 实现后端代码生成 Agent，自动生成 Spring Boot Controller、Service、Repository、Entity、DTO、启动类和 Maven 配置，并增加包名、文件完整性、Markdown 代码块等校验。
- 实现测试生成与自动修复链路，根据 Maven 测试报告驱动 CodeFixAgent 生成修复代码，形成“生成-测试-修复-再测试”的闭环。
- 实现 RuntimeAgent，对生成项目进行后端打包、前端依赖安装、前端构建、服务启动和 HTTP 健康检查，验证生成代码是否真正可运行。
```

## 6. 更偏 Java 后端岗位的写法

```text
AI 多智能体软件研发平台 | Java / Spring Boot / DeepSeek / RAG

- 基于 Spring Boot 设计多 Agent 编排平台，将需求分析、RAG 检索、代码生成、测试、修复和运行拆分为多个可插拔执行器。
- 设计 AgentState 和 WorkflowContext 作为任务上下文，统一管理 PRD 分析结果、知识库检索结果、代码生成结果、测试报告和运行结果。
- 封装 Maven、Npm、File、Terminal 等工具能力，实现 Agent 对本地工程的文件写入、构建测试和服务运行。
- 接入 DeepSeek API，基于 Prompt 模板驱动后端代码、前端代码和测试代码生成，并增加生成结果校验提升稳定性。
- 以订单管理系统为案例，自动生成 Spring Boot + React 前后端项目，并打通构建、测试、运行和健康检查链路。
```

## 7. 更偏 AI Agent 开发岗位的写法

```text
AI 多智能体软件研发平台 | Multi-Agent / RAG / LLMOps / Java

- 设计并实现面向软件研发场景的多智能体系统，包含 PRD Agent、RAG Agent、BackendCode Agent、FrontendCode Agent、Test Agent、CodeFix Agent 和 Runtime Agent。
- 通过工作流编排将大模型从一次性代码生成升级为“需求理解-知识增强-代码生成-测试反馈-自动修复”的闭环式 Agent 流程。
- 基于本地知识库实现 RAG 检索雏形，将团队规范、接口约束和代码风格注入代码生成 Prompt，减少模型幻觉和不符合规范的输出。
- 设计生成结果校验机制，对文件完整性、包名、非法路径、Markdown 代码块、前后端字段契约等问题进行拦截。
- 针对 RuntimeAgent 卡住、前后端字段不一致、测试环境 Mockito attach 失败等问题进行排查和修复，提升 Agent 工程链路稳定性。
```

## 8. 面试时 1 分钟介绍

```text
我做的是一个基于 Java/Spring Boot 的 AI 多智能体软件研发平台。它不是单纯调用大模型生成代码，而是把软件研发流程拆成多个 Agent：PRD 分析、RAG 检索、后端生成、前端生成、测试生成、自动修复和运行托管。每个 Agent 通过共享的 AgentState 传递上下文，由 AgentSupervisor 统一编排。项目以订单管理系统作为案例，能够从需求自动生成 Spring Boot 后端和 React 前端，并执行 Maven 测试、失败自动修复、最终启动前后端服务做健康检查。这个项目主要体现我对 Java 工程化、Agent 编排、LLM 调用、RAG 和自动化研发链路的理解。
```

## 9. 面试时 3 分钟介绍

```text
这个项目的背景是我想解决企业内部重复 CRUD 和脚手架开发的问题。单纯让大模型生成代码不稳定，所以我做了一个多 Agent 编排平台，把研发过程拆分成多个职责明确的 Agent。

入口是 WorkflowController，触发后由 AgentSupervisor 创建 WorkflowContext 和 AgentState。AgentState 是全链路共享上下文，用来传递 PRD、RAG、生成结果、测试报告和运行结果。执行顺序是 PrdAgent、RagAgent、BackendCodeAgent、FrontendCodeAgent、TestAgent、CodeFixAgent 和 RuntimeAgent。

其中 BackendCodeAgent 会生成 Spring Boot 的 Controller、Service、Repository、Entity、DTO、pom 和 application.yml；FrontendCodeAgent 会生成 React + TypeScript 页面、表单、表格和 API 请求。TestAgent 会读取后端代码生成 Controller 测试并执行 Maven test。如果测试失败，CodeFixAgent 会把错误日志和代码上下文交给大模型，让它返回修复后的代码。最后 RuntimeAgent 负责打包、构建、启动前后端并做健康检查。

项目过程中我也遇到了一些真实工程问题，比如 RuntimeAgent 启动后端和前端时，由于 mvn spring-boot:run 和 npm run dev 是常驻进程，如果同步 waitFor 就会卡住；还有前端生成了 user_id、product_id 这种下划线字段，而后端 Jackson 默认接收 userId、productId，导致 JPA 非空约束异常。针对这些问题，我增加了日志追踪、字段契约校验，并计划进一步做异步任务、状态持久化、日志可视化和安全沙箱。

所以这个项目不是为了替代 LangGraph 或 CrewAI，而是面向 Java 企业研发场景，自己实现一个可解释、可调试、可和 Spring Boot 工程深度集成的 Agent 平台原型。
```

## 10. 项目价值如何表达

建议表达：

```text
这个项目的价值不是生成一个订单系统，而是把 Agent 编排、RAG、代码生成、测试反馈、自动修复、构建运行这些能力整合成一个 Java 工程化闭环。它展示了我不仅会调用大模型 API，也能把大模型能力落到真实研发流程中。
```

不要这样表达：

```text
我做了一个能完全替代程序员的自动开发平台。
```

原因：

- 这会显得夸张。
- 面试官很容易追问生产稳定性、安全、评测、权限、回滚、审计。
- 当前项目还处于企业级原型阶段。

## 11. 可以量化的成果

当前可以写：

```text
- 完成 7 个核心 Agent 的职责拆分和串联。
- 打通从需求到订单管理系统运行的端到端链路。
- 自动生成 Spring Boot 后端和 React 前端项目。
- 后端测试、前端构建、Agent 平台测试均可通过。
- 实现测试失败后的自动修复流程雏形。
```

后续补齐后可以写：

```text
- 支持任务异步执行和进度查询。
- 支持 Agent 执行日志落库和可视化追踪。
- 支持失败任务从指定步骤重跑。
- 支持多任务 workspace 隔离。
- 支持基于 OpenAPI 的前后端契约生成。
```

## 12. 简历上建议放的位置

如果你投 Java 开发：

放在项目经历第一或第二个，标题突出 Java 和工程化。

如果你投 AI Agent 开发：

放在第一个，标题突出 Multi-Agent、RAG、LLM 工程化。

## 13. 风险点和诚实说法

面试官可能问：这个项目能不能生产使用？

建议回答：

```text
目前我会把它定位为企业级原型，不会直接说是生产级平台。它已经打通了多 Agent 软件研发闭环，但生产化还需要补齐任务异步化、状态持久化、权限、安全沙箱、日志追踪、人工审批、评测体系和成本控制。我接下来正在按这些方向继续完善。
```

## 14. 简历最终版本示例

```text
AI 多智能体软件研发平台 | Java 17、Spring Boot 3、DeepSeek、RAG、React、TypeScript

项目描述：基于 Java/Spring Boot 设计并实现面向企业研发场景的 AI 多智能体软件研发平台，支持从自然语言需求出发，自动完成 PRD 分析、知识库检索、前后端代码生成、测试生成、自动修复、构建运行和健康检查，并以订单管理系统作为端到端案例验证。

核心工作：
- 设计 AgentSupervisor 工作流编排器，将 PRD、RAG、后端生成、前端生成、测试、修复、运行拆分为 7 个职责明确的 Agent。
- 设计 AgentState 共享上下文，统一传递 PRD 分析结果、RAG 检索结果、代码生成结果、测试报告和运行结果。
- 封装 DeepSeek API 和 Prompt 模板机制，实现后端代码、前端代码、测试代码和修复代码的结构化生成。
- 实现 Maven/Npm/File/Terminal 工具链，支持生成项目的文件写入、测试执行、构建启动和 HTTP 健康检查。
- 针对模型输出不稳定问题增加生成文件完整性、包名、非法路径、Markdown 代码块、前后端字段契约等校验。
- 排查并修复 RuntimeAgent 服务启动阻塞、前后端字段不一致导致 JPA 非空异常等真实工程问题。
```
