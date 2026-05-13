package com.example.agent.agents;

import com.example.agent.state.AgentState;
import org.springframework.stereotype.Component;

@Component
public class TestAgent implements BaseAgent {
    @Override
    public AgentState execute(AgentState state) {
        System.out.println("TestAgent 执行了");
        return state;
    }

    @Override
    public String getAgentName() {
        return "TestAgent";
    }
}