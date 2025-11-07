# Travel Agent (Semantic Kernel + Spring Boot)

This project mirrors the Python Semantic Kernel agent from the **AI Agents for Beginners** series, but it is implemented with **Java 17**, **Spring Boot 3.5.7**, and **Semantic Kernel for Java**.

## Features

- Spring-managed `Kernel` that wires the OpenAI chat completion service and exposes a travel tool plugin.
- Conversation-aware `TravelAgentService` that maintains chat history and delegates reasoning to Semantic Kernel.
- REST endpoint (`POST /api/agent/chat`) that accepts a conversation id + user message and responds with the assistant reply plus the running history.

## Getting Started

1. Export credentials (replace with your key/endpoint):
   ```bash
   export OPENAI_API_KEY="sk-..."
   export SEMANTIC_KERNEL__DEFAULT_MODEL="gpt-4o-mini"
   ```
2. Build & run:
   ```bash
   mvn spring-boot:run
   ```
3. Chat with the agent:
   ```bash
   curl -X POST http://localhost:8080/api/agent/chat \
     -H "Content-Type: application/json" \
     -d '{"conversationId": "demo", "message": "Plan a 4-day trip to Paris in May"}'
   ```
4. Open the browser UI at <http://localhost:8080/> to use the built-in chat surface.

## Next Steps

- Replace the mock `TravelInfoTool` with real data sources (flights, hotels, calendar).
- Persist conversations and memories via Semantic Kernel's memory store interfaces.
- Stream responses (Server-Sent Events or WebSocket) for better UX.
