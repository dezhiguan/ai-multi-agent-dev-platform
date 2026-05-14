package com.example.agent.controller;


import com.example.agent.agents.PrdAgent;
import com.example.agent.state.AgentState;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/prd")
@RequiredArgsConstructor
public class PrdController {

    private final PrdAgent prdAgent;

    @PostMapping("/analyze")
    public AgentState analyze(@RequestParam String prdContent) {
        AgentState agentState = new AgentState();
        agentState.setPrdContent(prdContent);
        return prdAgent.execute(agentState);
    }
}
