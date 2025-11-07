package com.example.travelagent.agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConversationState {

    private final List<AgentMessage> messages = Collections.synchronizedList(new ArrayList<>());

    public void addMessage(AgentMessage message) {
        messages.add(message);
    }

    public List<AgentMessage> snapshot() {
        synchronized (messages) {
            return List.copyOf(messages);
        }
    }
}
