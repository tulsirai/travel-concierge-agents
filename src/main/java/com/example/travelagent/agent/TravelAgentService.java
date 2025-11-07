package com.example.travelagent.agent;

import com.example.travelagent.controller.dto.ChatRequest;
import com.example.travelagent.controller.dto.ChatResponse;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.orchestration.PromptExecutionSettings;
import com.microsoft.semantickernel.services.ServiceNotFoundException;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import com.microsoft.semantickernel.services.chatcompletion.ChatMessageContent;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class TravelAgentService {

    private static final String SYSTEM_PROMPT = """
            You are a helpful travel concierge. Use the travelTools plugin when \
            you need destination facts, then craft a concise plan with next steps.
            """;

    private final Kernel kernel;
    private final Map<String, ConversationState> conversations = new ConcurrentHashMap<>();

    public ChatResponse handleTurn(ChatRequest request) {
        String conversationId = resolveConversationId(request.getConversationId());
        ConversationState state = conversations.computeIfAbsent(conversationId, id -> new ConversationState());

        state.addMessage(AgentMessage.user(request.getMessage()));

        ChatHistory history = buildHistory(state);
        PromptExecutionSettings settings = PromptExecutionSettings.builder()
                .withTemperature(0.4)
                .withTopP(0.9)
                .withMaxTokens(400)
                .build();
        InvocationContext invocationContext = InvocationContext.builder()
                .withPromptExecutionSettings(settings)
                .build();

        ChatCompletionService chatService = getChatService();
        List<ChatMessageContent<?>> responses = chatService
                .getChatMessageContentsAsync(history, kernel, invocationContext)
                .block();

        if (responses == null || responses.isEmpty()) {
            throw new IllegalStateException("Model returned no content.");
        }

        String reply = responses.get(0).getContent();
        state.addMessage(AgentMessage.assistant(reply));

        return ChatResponse.builder()
                .conversationId(conversationId)
                .reply(reply)
                .history(state.snapshot())
                .build();
    }

    private String resolveConversationId(String provided) {
        if (StringUtils.hasText(provided)) {
            return provided;
        }
        return UUID.randomUUID().toString();
    }

    private ChatHistory buildHistory(ConversationState state) {
        ChatHistory history = new ChatHistory(SYSTEM_PROMPT);
        state.snapshot().forEach(message -> {
            switch (message.role()) {
                case "user" -> history.addUserMessage(message.content());
                case "assistant" -> history.addAssistantMessage(message.content());
                default -> log.warn("Unknown role {} ignored", message.role());
            }
        });
        return history;
    }

    private ChatCompletionService getChatService() {
        try {
            return kernel.getService(ChatCompletionService.class);
        } catch (ServiceNotFoundException e) {
            throw new IllegalStateException("ChatCompletionService is not registered in the Kernel.", e);
        }
    }
}
