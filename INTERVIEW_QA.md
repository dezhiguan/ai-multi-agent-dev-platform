# 面试题与回答话术

## 1. 项目整体类问题

### Q1：请介绍一下你的 AI 多智能体项目

回答：

```text
这个项目是一个基于 Java/Spring Boot 的 AI 多智能体软件研发平台。它的目标是把自然语言需求转化为可运行的前后端项目。平台把研发流程拆成多个 Agent，包括 PRD 分析、RAG 检索、后端代码生成、前端代码生成、测试生成、自动修复和运行托管。每个 Agent 只负责一个明确职责，AgentSupervisor 负责编排执行，AgentState 负责在 Agent 之间传递上下文。当前项目以订单管理系统作为端到端案例，已经能生成 Spring Boot 后端和 React 前端，并执行测试、修复、构建和健康检查。
```

### Q2：这个项目和普通代码生成工具有什么区别

回答：

```text
普通代码生成工具往往是一次性生成代码，生成完就结束。我的项目更强调工程闭环：先分析需求，再通过 RAG 检索规范，然后生成前后端代码，接着生成测试并执行，如果测试失败还会进入修复 Agent，最后构建并启动服务做健康检查。所以它不是单点代码生成，而是一个从需求到运行的 Agent 工作流。
```

### Q3：为什么要拆成多个 Agent

回答：

```text
因为软件研发流程本身就是多阶段的。PRD 分析、知识检索、后端生成、前端生成、测试、修复、运行的输入输出都不一样。如果全部交给一个大 Prompt，会导致上下文混乱、输出不可控、错误难定位。拆成多个 Agent 后，每个 Agent 的职责、Prompt、输入输出和校验规则都更清晰，也方便后续替换模型、增加重试、记录日志和单独优化。
```

### Q4：Agent 之间如何传递数据

回答：

```text
我设计了 AgentState 作为共享上下文。比如 PrdAgent 把结构化 PRD 写入 AgentState，RagAgent 读取 PRD 中的 queryContent 去检索知识库，再把 RagContext 写回 AgentState。BackendCodeAgent 和 FrontendCodeAgent 再读取 PRD 和 RAG 结果生成代码。TestAgent 会把测试报告写入 TestExecutionResult，CodeFixAgent 再根据测试报告和源代码修复。这样每个 Agent 都不需要直接依赖其他 Agent，只依赖统一的状态对象。
```

## 2. 架构和流程类问题

### Q5：项目的核心架构是什么

回答：

```text
核心架构分三层。第一层是 Controller 层，提供工作流触发接口。第二层是 Workflow 层，核心是 AgentSupervisor 和 WorkflowContext，负责流程编排、步骤记录和错误中断。第三层是 Agent 和 Tool 层，Agent 负责具体任务，Tool 负责文件写入、Maven、Npm、终端命令、LLM 调用和 RAG 检索。生成出来的业务系统放在 business-workspace 下，与平台本体隔离。
```

### Q6：AgentSupervisor 做了什么

回答：

```text
AgentSupervisor 是总调度器。它初始化 AgentState 和 WorkflowContext，然后按固定顺序执行 PrdAgent、RagAgent、BackendCodeAgent、FrontendCodeAgent、TestAgent、CodeFixAgent 和 RuntimeAgent。每执行一个步骤，会设置当前步骤、调用对应 Agent、记录完成步骤，并检查 AgentState 中有没有错误。如果测试失败，它会触发 CodeFixAgent，最多重试两次。
```

### Q7：为什么现在用固定顺序，而不是动态规划

回答：

```text
当前项目定位是软件研发流水线，步骤之间有明确依赖，比如没有 PRD 就不能检索知识，没有后端代码就不能生成测试，没有测试报告就不能修复。因此第一阶段选择固定顺序可以保证链路稳定、便于调试。后续可以把 WorkflowStep 抽象成 DAG，支持条件分支、并行执行和人工审批。
```

### Q8：工作流现在有什么缺陷

回答：

```text
主要缺陷是同步执行、状态主要在内存、进度不可视化、失败后不能断点恢复。真实企业场景下，我会把工作流改成异步任务，任务和步骤状态落库，增加 status 和 logs 接口，支持失败后从指定步骤重跑，并在关键步骤加入人工审批。
```

## 3. LLM 和 Prompt 类问题

### Q9：你是怎么调用大模型的

回答：

```text
我封装了 LlmClient，统一读取模型 API 地址、API Key 和模型名称，然后通过 RestTemplate 调用 DeepSeek Chat API。所有 Agent 不直接关心 HTTP 细节，只传入 prompt，拿到模型返回内容。这样后续如果要切换模型、增加重试、超时、限流或成本统计，只需要扩展 LlmClient。
```

### Q10：Prompt 怎么管理

回答：

```text
Prompt 放在 resources/prompts 目录下，每个 Agent 一个模板，比如 prd-agent、backend-code-agent、frontend-code-agent、test-agent 和 code-fix-agent。PromptUtil 负责加载模板并替换变量。这样 Prompt 和 Java 业务逻辑分离，方便单独调优和做版本管理。
```

### Q11：如何保证大模型输出格式正确

回答：

```text
我做了两层控制。第一层是在 Prompt 中明确要求输出 JSON 或指定文件内容，不要输出 Markdown 代码块和解释。第二层是在 Java 侧做校验，比如 BackendCodeAgent 会检查文件集合是否完整、包名是否正确、是否包含 Markdown 代码块、是否包含错误包名；FrontendCodeAgent 会检查必需文件、非法路径和字段契约。模型输出不符合规则就中断，不让错误代码进入后续流程。
```

### Q12：如果模型总是不听 Prompt 怎么办

回答：

```text
不能只靠 Prompt。我的思路是 Prompt 约束加程序校验，再加自动修复。比如前端字段名问题，Prompt 要明确告诉模型后端使用驼峰字段；Java 侧还要校验是否出现 user_id、product_id 这样的错误字段。如果仍然失败，可以根据后端 DTO 自动生成字段契约，甚至用 OpenAPI 生成 TypeScript 类型，从源头减少模型自由发挥。
```

## 4. RAG 类问题

### Q13：为什么需要 RAG

回答：

```text
因为企业代码生成不能只依赖模型通用知识。企业会有自己的接口规范、代码风格、异常处理规范、目录结构和安全要求。RAG 的作用是把这些私有知识检索出来，注入到生成 Prompt 中，让模型按团队规范生成代码。
```

### Q14：你现在的 RAG 做到什么程度

回答：

```text
当前是雏形，包含文档解析、切片、embedding 和向量存储服务的基础结构，也有 RagAgent 从 PRD 中取 queryContent 去检索知识。后续我会重点完善知识库上传、切片策略、向量检索评分、检索结果可视化和基于规范的生成效果评测。
```

### Q15：RAG 检索结果不准怎么办

回答：

```text
我会从四个方向处理。第一是优化切片，避免切得太碎或太长。第二是增加 metadata，比如文档类型、业务域、版本。第三是混合检索，把关键词检索和向量检索结合。第四是在生成前让模型判断检索结果是否相关，必要时重新检索。
```

## 5. 代码生成类问题

### Q16：BackendCodeAgent 怎么设计的

回答：

```text
BackendCodeAgent 会读取 PRD 分析结果和 RAG 上下文，拼接 backend-code-agent prompt，调用 LLM 得到一组 Java 文件内容，然后校验文件是否包含 Order、Repository、DTO、Service、Controller 等必需文件。校验通过后写入 order-service 的源码目录，并补充 pom.xml、启动类和 application.yml。
```

### Q17：FrontendCodeAgent 怎么设计的

回答：

```text
FrontendCodeAgent 读取 PRD 分析结果，调用 LLM 生成 React + TypeScript 前端文件，包括页面、表单、表格、API 封装和类型定义。它还负责写入 Vite 工程骨架，比如 package.json、index.html、vite.config.ts 和 tsconfig。后续我会让它基于后端 DTO 或 OpenAPI 自动生成字段契约，避免前后端字段不一致。
```

### Q18：你遇到过什么代码生成问题

回答：

```text
一个典型问题是前端生成了 user_id、product_id、total_price 这种下划线字段，但后端 Spring Boot Jackson 默认接收的是 userId、productId、totalPrice。结果创建订单时 productId 是 null，JPA 保存时报 not-null property references a null value。这个问题说明模型生成代码必须有契约约束，不能只靠自然语言提示。我后续的方案是从后端 DTO 自动解析字段并注入前端 Prompt，同时在校验阶段拦截错误字段。
```

## 6. 测试和自动修复类问题

### Q19：TestAgent 的价值是什么

回答：

```text
TestAgent 的价值是把代码生成从“看起来能用”推进到“可以被测试验证”。它会读取后端核心代码，让模型生成 Controller 测试，然后执行 Maven test。测试结果会写回 AgentState，作为后续 RuntimeAgent 是否运行、CodeFixAgent 是否修复的依据。
```

### Q20：CodeFixAgent 怎么工作

回答：

```text
CodeFixAgent 会读取测试失败日志和后端源代码，拼接 code-fix-agent prompt，让模型返回修复后的文件内容。然后它会校验修复结果是否为空、文件是否在白名单内、包名是否正确、是否包含 Markdown 代码块，校验通过后覆盖写入源文件。之后工作流会重新执行测试。
```

### Q21：自动修复会不会有风险

回答：

```text
会有风险。模型可能改错文件、引入新问题，甚至删除业务逻辑。所以生产化时不能直接无条件覆盖。我的改进方向是让 CodeFixAgent 生成 patch 或 diff，记录修复前后差异，限制可修改文件白名单，并在关键场景加入人工审批。
```

## 7. RuntimeAgent 和排障类问题

### Q22：RuntimeAgent 为什么容易卡住

回答：

```text
因为 mvn spring-boot:run 和 npm run dev 都是长期运行的服务命令。如果通过 Process.waitFor 或封装的 terminalTool.exec 同步等待命令结束，RuntimeAgent 就会一直阻塞。正确做法是后台启动进程，立即返回 pid，把日志重定向到文件，然后由 RuntimeAgent 单独执行 HTTP 健康检查。
```

### Q23：页面操作报错你怎么排查

回答：

```text
我会分三层排查。第一看浏览器 Network，确认请求 URL、状态码和 Request Payload。第二看后端 backend.log，定位 Controller、Service 或 JPA 异常。第三用 curl 直接请求 8090 后端接口，判断是前端传参问题还是后端逻辑问题。比如创建订单失败时，我通过日志看到 productId 为 null，再结合 Network 发现前端传的是 product_id，从而定位到前后端字段契约不一致。
```

### Q24：怎么判断服务是卡住还是正常运行

回答：

```text
我会看三个信号。第一是平台日志，看 RuntimeAgent 卡在哪一步。第二是端口，比如 lsof -i :8090 或 lsof -i :5173，看服务是否 LISTEN。第三是服务日志，比如 backend.log 和 frontend.log，看是否出现 Started、Tomcat started、Vite ready 或 ERROR。如果端口已经监听但工作流不往下走，通常是启动命令没有返回或健康检查地址不对。
```

## 8. Java 后端基础追问

### Q25：为什么 JPA 会报 not-null property references a null value

回答：

```text
因为实体字段上有 nullable = false，Hibernate 在 flush 或 save 时会做非空检查。如果请求 DTO 中 productId 没有正确绑定，Service 把 null 设置给 Entity 的 productId，保存时就会触发 PropertyValueException。根因通常是前端字段名和后端 DTO 字段名不一致，或者请求体没有传这个字段。
```

### Q26：Spring Boot 默认 JSON 字段名是什么

回答：

```text
Spring Boot 默认使用 Jackson。Java 字段如果是 userId，默认 JSON 字段也是 userId，不会自动变成 user_id，除非配置了 PropertyNamingStrategies.SNAKE_CASE 或使用 @JsonProperty。因此前端必须按后端 DTO 的字段名传参。
```

### Q27：为什么 Controller 推荐构造器注入

回答：

```text
构造器注入可以让依赖不可变，便于测试，也能在对象创建时就暴露缺失依赖问题。相比字段注入，构造器注入更清晰，也更适合写单元测试，比如我可以直接 new OrderController(fakeOrderService)，不依赖 Spring 容器或 Mockito。
```

## 9. 与主流 Agent 框架对比

### Q28：和 LangGraph 比，你这个项目有什么区别

回答：

```text
LangGraph 是成熟的 Agent 编排框架，重点能力包括 durable execution、human-in-the-loop、memory、streaming 和可恢复长任务。我的项目不是为了替代 LangGraph，而是用 Java/Spring Boot 实现面向企业 Java 研发流程的 Agent 平台原型。优势是和 Spring Boot、Maven、Java 工程、企业内部系统集成更自然；劣势是还缺少 LangGraph 那种成熟的 checkpoint、streaming、可视化和复杂图编排能力。
```

### Q29：和 CrewAI 比呢

回答：

```text
CrewAI 提供 Agent、Task、Flow、guardrails、memory、observability 和企业部署能力，生态更成熟。我的项目优势在于 Java 技术栈和软件研发场景更聚焦，比如直接生成 Spring Boot 项目、执行 Maven 测试、启动 Java 服务。劣势是抽象还不够通用，也没有成熟的工具生态和可视化平台。
```

### Q30：为什么不直接用成熟框架

回答：

```text
如果是生产效率优先，我不会排斥成熟框架。但这个项目的目标是理解和掌握 Agent 工程化底层能力，并探索 Java 企业研发场景的深度集成。很多成熟框架偏 Python 或通用 Agent 场景，而企业 Java 团队往往需要和 Spring Boot、Maven、权限、审计、私有知识库、内部规范深度结合。所以我选择自己实现核心链路，同时借鉴成熟框架在状态持久化、human-in-the-loop、observability 等方面的设计。
```

## 10. 生产化问题

### Q31：这个项目现在能生产使用吗

回答：

```text
目前我会把它定位为企业级原型，还不能直接生产使用。它已经打通了 Agent 软件研发闭环，但生产化还需要补齐异步任务、状态持久化、日志追踪、安全沙箱、权限控制、人工审批、模型成本控制、评测体系和任务隔离。
```

### Q32：如果继续做，你优先做什么

回答：

```text
我会优先做三个方向。第一是工作流异步化和进度可视化，让任务状态可查询。第二是状态和日志落库，让每个 Agent 的输入、输出、耗时、错误都可追溯。第三是运行安全和任务隔离，比如每个 taskId 独立 workspace、命令白名单、Docker 沙箱和端口动态分配。
```

### Q33：如何做 Agent 可观测性

回答：

```text
我会记录四类数据。第一是任务级数据，比如 taskId、需求、状态、开始结束时间。第二是步骤级数据，比如 Agent 名称、当前状态、耗时、错误。第三是模型调用数据，比如 prompt、模型输出、token、耗时和失败原因。第四是工具调用数据，比如执行了什么命令、写了哪些文件、命令输出是什么。这样才能定位问题是 Prompt、模型、工具还是业务代码。
```

### Q34：如何做安全控制

回答：

```text
Agent 平台最危险的是文件写入和命令执行。我会做路径白名单，限制只能写入当前 task workspace；做命令白名单，只允许 mvn、npm、java 等必要命令；生成项目放到 Docker 沙箱执行；对模型输出做文件名、路径、包名、内容校验；关键操作加人工审批和审计日志。
```

## 11. 深度追问

### Q35：为什么需要 AgentState，而不是直接方法返回值串起来

回答：

```text
方法返回值适合简单链路，但多 Agent 场景需要共享更多上下文，比如 PRD、RAG、代码生成结果、测试结果、错误列表和运行结果。AgentState 可以统一承载这些状态，也便于后续持久化、断点续跑和可视化。如果后续做 DAG 或重试，AgentState 比层层传参更容易扩展。
```

### Q36：如何支持多任务并发

回答：

```text
当前固定写入 business-workspace/order-service 和 order-web，不适合多任务并发。改造方案是每个 taskId 创建独立 workspace，比如 business-workspace/tasks/{taskId}/order-service，并动态分配端口。任务状态落库，工作流用线程池或消息队列异步执行。这样不同任务不会互相覆盖文件和端口。
```

### Q37：如何从失败步骤恢复

回答：

```text
需要把每个步骤的输入输出、状态和生成文件记录下来。恢复时可以从失败步骤重新执行，也可以从上一个成功 checkpoint 继续。对于有副作用的步骤，比如文件写入和命令执行，需要记录产物和幂等性策略，避免重复执行造成覆盖或脏状态。
```

### Q38：如何评估 Agent 生成质量

回答：

```text
我会从工程指标和业务指标两类评估。工程指标包括编译通过率、测试通过率、自动修复成功率、平均耗时、失败步骤分布、token 成本。业务指标包括接口是否符合 PRD、字段契约是否一致、代码规范是否满足知识库约束。后续可以维护一组标准 PRD 作为评测集，用来比较 Prompt 和模型版本。
```

### Q39：如何处理模型幻觉

回答：

```text
模型幻觉不能靠一句 Prompt 解决。我会用 RAG 限定上下文，用结构化输出约束格式，用程序校验拦截非法结果，用测试执行验证行为，用自动修复处理可修复错误。对于高风险操作，再加人工审批。也就是说，要用工程手段把模型不确定性限制在可控范围内。
```

### Q40：你从这个项目里学到什么

回答：

```text
我最大的收获是 Agent 项目的难点不只是调用模型，而是工程化闭环。比如状态怎么传、错误怎么追、日志怎么看、模型输出怎么校验、失败怎么恢复、生成代码怎么测试、运行进程怎么管理。这些问题都是真实落地时必须解决的。通过这个项目，我对 Java 后端和 AI Agent 结合的边界、价值和风险有了更具体的理解。
```

## 12. 面试官可能质疑的问题

### Q41：这个项目是不是只是包装了一层大模型 API

回答：

```text
不是。调用大模型只是其中一环。这个项目更核心的是工作流编排、状态传递、工具调用、文件生成、测试执行、错误反馈、自动修复和运行验证。大模型负责生成内容，但平台负责把生成内容纳入工程流程，并用校验、测试和日志把它变得可控。
```

### Q42：订单系统太简单，项目价值在哪里

回答：

```text
订单系统只是验证案例，项目价值不在订单业务本身，而在 Agent 平台链路。类似订单、商品、库存、用户管理这类企业后台模块都可以通过同一套流程生成。后续真正要增强的是需求泛化、契约生成、知识库规范注入和多任务隔离。
```

### Q43：如果公司已经有低代码平台，你这个有什么意义

回答：

```text
低代码平台更适合配置化 CRUD，而 Agent 平台更适合把自然语言需求、企业知识库、代码生成、测试修复和研发流程结合起来。它可以生成真实代码，进入现有 Git、CI、测试和部署流程。两者不是完全替代关系，Agent 可以作为低代码之外的研发自动化补充。
```

## 13. 反问面试官

可以反问：

```text
贵团队现在的 Agent 应用主要是偏业务助手、研发提效，还是内部平台建设？
```

```text
如果做企业内部 Agent 平台，团队更关注模型效果，还是更关注权限、安全、可观测性和流程集成？
```

```text
贵团队 Java 技术栈下的 Agent 应用，是更倾向接入成熟框架，还是自研适配内部系统？
```

## 14. 参考资料

- LangGraph overview: https://docs.langchain.com/langgraph
- LangGraph durable execution: https://docs.langchain.com/oss/python/langgraph/durable-execution
- Microsoft Agent Framework overview: https://learn.microsoft.com/en-us/agent-framework/overview/
- CrewAI docs: https://docs.crewai.com/
