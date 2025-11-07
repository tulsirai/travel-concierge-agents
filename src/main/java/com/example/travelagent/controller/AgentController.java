package com.example.travelagent.controller;

import com.example.travelagent.agent.TravelAgentService;
import com.example.travelagent.controller.dto.ChatRequest;
import com.example.travelagent.controller.dto.ChatResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/agent", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AgentController {

    private final TravelAgentService travelAgentService;

    @PostMapping(path = "/chat", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
        return travelAgentService.handleTurn(request);
    }
}
