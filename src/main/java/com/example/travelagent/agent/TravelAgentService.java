package com.example.travelagent.agent;

import com.example.travelagent.controller.dto.ChatRequest;
import com.example.travelagent.controller.dto.ChatResponse;
import com.example.travelagent.tool.TravelInfoTool;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class TravelAgentService {

    private static final String SYSTEM_PROMPT = """
            You are a helpful travel concierge. Use the travel tools context when \
            you need destination facts, then craft a concise plan with next steps.
            """;

    private final ChatModel chatModel;
    private final TravelInfoTool travelInfoTool;
    private final AzureOpenAiChatOptions defaultChatOptions;
    private final Map<String, ConversationState> conversations = new ConcurrentHashMap<>();

    public ChatResponse handleTurn(ChatRequest request) {
        String conversationId = resolveConversationId(request.getConversationId());
        ConversationState state = conversations.computeIfAbsent(conversationId, id -> new ConversationState());

        state.addMessage(AgentMessage.user(request.getMessage()));
        List<AgentMessage> snapshotBeforeReply = state.snapshot();

        Prompt prompt = new Prompt(
                buildPromptMessages(snapshotBeforeReply, request.getMessage()),
                AzureOpenAiChatOptions.fromOptions(defaultChatOptions)
        );
        org.springframework.ai.chat.model.ChatResponse aiResponse = chatModel.call(prompt);

        String reply = extractReply(aiResponse);
        state.addMessage(AgentMessage.assistant(reply));

        return ChatResponse.builder()
                .conversationId(conversationId)
                .reply(reply)
                .history(state.snapshot())
                .build();
    }

    private List<Message> buildPromptMessages(List<AgentMessage> history, String latestUserMessage) {
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(SYSTEM_PROMPT));

        travelInfoTool.detectFact(latestUserMessage)
                .ifPresent(fact -> messages.add(new SystemMessage(
                        "travelTools.destination_facts(\"" + fact.city() + "\") => " + fact.advice()
                )));

        history.forEach(message -> {
            switch (message.role()) {
                case "user" -> messages.add(new UserMessage(message.content()));
                case "assistant" -> messages.add(new AssistantMessage(message.content()));
                default -> log.warn("Unknown role {} ignored", message.role());
            }
        });
        return messages;
    }

    private String extractReply(org.springframework.ai.chat.model.ChatResponse aiResponse) {
        if (aiResponse == null || aiResponse.getResult() == null || aiResponse.getResult().getOutput() == null) {
            throw new IllegalStateException("Model returned no content.");
        }
        return aiResponse.getResult().getOutput().getText();
    }

    private String resolveConversationId(String provided) {
        if (StringUtils.hasText(provided)) {
            return provided;
        }
        return UUID.randomUUID().toString();
    }
}
