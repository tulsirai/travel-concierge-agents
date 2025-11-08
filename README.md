# Travel Agent (Spring AI + Spring Boot)

This project mirrors the Python Semantic Kernel agent from the **AI Agents for Beginners** series, but it is now implemented with **Java 17**, **Spring Boot 3.5.7**, and **Spring AI 1.0.3**.

## Features

- Spring AI-powered chat client that wires the OpenAI/Azure OpenAI chat completion service and injects a travel tool context.
- Conversation-aware `TravelAgentService` that maintains chat history and delegates reasoning to Spring AI.
- REST endpoint (`POST /api/agent/chat`) that accepts a conversation id + user message and responds with the assistant reply plus the running history.

## Getting Started

1. Export credentials (replace with your key/endpoint):
   ```bash
   export SPRING_AI_AZURE_OPENAI_API_KEY="sk-..."
   export SPRING_AI_AZURE_OPENAI_ENDPOINT="https://your-resource-name.openai.azure.com/"
   export SPRING_AI_AZURE_OPENAI_DEPLOYMENT_NAME="gpt-4o-mini" # optional override
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
- Persist conversations and memories via Spring AI's memory abstractions or your own datastore.
- Stream responses (Server-Sent Events or WebSocket) for better UX.
