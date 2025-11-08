# Travel Agent Architecture & Interaction Flow

The travel-agent app exposes a simple chat UI backed by a Spring Boot API. Regardless of whether the branch uses Semantic Kernel or Spring AI, the high-level interaction flow stays the same:

```
User
  |
  v
Browser UI (static/index.html)
  |  POST /api/agent/chat {conversationId, message}
  v
REST API (AgentController)
  |  travelAgentService.handleTurn(...)
  v
TravelAgentService
  |-- store user msg --> ConversationState
  |-- call -----------|--> TravelInfoTool (facts, optional)
  |-- build prompt + history + facts
  |-- call -----------|--> ChatModel / Kernel
  |<-- reply ----------|
  |-- store assistant msg --> ConversationState
  v
REST API returns ChatResponse
  |
  v
Browser UI renders assistant reply
```

## Component Responsibilities

| Layer | File(s) | Responsibilities |
|-------|---------|------------------|
| **Presentation** | `src/main/resources/static/index.html` | Minimal single-page chat UI. Manages conversation IDs, sends AJAX requests, renders bubbles, and resets sessions. |
| **API Layer** | `AgentController` + DTOs (`ChatRequest`, `ChatResponse`) | Validates input, exposes POST `/api/agent/chat`, marshals responses back to the UI. |
| **Agent Orchestration** | `TravelAgentService`, `ConversationState`, `AgentMessage` | Owns the agent loop: maintains per-conversation history, invokes tools, builds prompts, calls the model, and persists the assistant reply. |
| **Tooling** | `TravelInfoTool` | Encapsulates structured knowledge (destination facts). Exposed as a Semantic Kernel plugin on `main`, or injected manually into the prompt on `spring-ai-migration`. |
| **LLM Integration** | `SemanticKernelConfig` (main) or `SpringAiConfig` + `AzureOpenAiProperties` (spring-ai-migration) | Wires the chat completion engine: Semantic Kernel `Kernel` with `OpenAIChatCompletion` vs. Spring AI `ChatModel` backed by Azure OpenAI. Handles credentials, model IDs, and default generation parameters. |
| **Configuration** | `application.yml`, `.env` | Captures model/endpoint settings. `.env` seeds Azure OpenAI secrets for local runs. |

## Branch-Specific Notes

- **`main` branch (Semantic Kernel)**  
  - `SemanticKernelConfig` creates an `OpenAIAsyncClient`, `OpenAIChatCompletion`, and `Kernel` with the `TravelInfoTool` plugin.  
  - `TravelAgentService` builds a `ChatHistory`, sets `PromptExecutionSettings`, and calls `chatService.getChatMessageContentsAsync(...).block()`.

- **`spring-ai-migration` branch (Spring AI)**  
  - `SpringAiConfig` binds Azure settings via `AzureOpenAiProperties`, builds an `OpenAIClientBuilder`, default `AzureOpenAiChatOptions`, and a `ChatModel`.  
  - `TravelAgentService` uses Spring AI `Message` classes (`SystemMessage`, `UserMessage`, `AssistantMessage`) and constructs a `Prompt` before calling `chatModel.call(prompt)`. Tool facts are injected as extra system context when `TravelInfoTool.detectFact` hits.

## Extensibility Hooks

- **Additional tools:** Add new methods to `TravelInfoTool` (or separate components) and either annotate them (Semantic Kernel) or inject results into prompts (Spring AI). Future Spring AI releases will allow direct function-calling registration.
- **Memory persistence:** Replace `ConversationState`’s in-memory map with a repository (SQL/Redis) or Semantic Kernel memory store for long-lived sessions.
- **Streaming responses:** Swap the REST controller for SSE/WebSocket to stream partial responses to the UI.

This layered view should help traditional engineers map familiar concepts (controller, service, repository-like tool) to the agentic loop implemented here.
