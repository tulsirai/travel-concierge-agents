package com.example.travelagent.controller.dto;

import com.example.travelagent.agent.AgentMessage;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ChatResponse {
    String conversationId;
    String reply;
    List<AgentMessage> history;
}
