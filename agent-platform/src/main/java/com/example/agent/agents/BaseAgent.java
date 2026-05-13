package com.example.agent.agents;

import com.example.agent.state.AgentState;

/**
 * 所有Agent的统一父接口
 * 多Agent架构核心
 */
public interface BaseAgent {

    /**
     * Agent执行入口
     * @param state 全局共享状态（输入+输出）
     * @return 更新后的状态
     */
    AgentState execute(AgentState state);

    /**
     * 获取Agent名称（用于日志/调度）
     */
    String getAgentName();
}