package com.example.travelagent.agent;

import java.time.Instant;

public record AgentMessage(String role, String content, Instant timestamp) {

    public static AgentMessage user(String content) {
        return new AgentMessage("user", content, Instant.now());
    }

    public static AgentMessage assistant(String content) {
        return new AgentMessage("assistant", content, Instant.now());
    }
}
