package com.example.travelagent.config;

import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.AzureKeyCredential;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.model.tool.DefaultToolCallingManager;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
@EnableConfigurationProperties(AzureOpenAiProperties.class)
public class SpringAiConfig {

    @Bean
    public OpenAIClientBuilder openAIClientBuilder(AzureOpenAiProperties properties) {
        if (!StringUtils.hasText(properties.getApiKey())) {
            throw new IllegalStateException("AZURE_OPENAI_API_KEY (or SPRING_AI_AZURE_OPENAI_API_KEY) is required.");
        }
        if (!StringUtils.hasText(properties.getEndpoint())) {
            throw new IllegalStateException("AZURE_OPENAI_ENDPOINT (or SPRING_AI_AZURE_OPENAI_ENDPOINT) is required.");
        }
        return new OpenAIClientBuilder()
                .credential(new AzureKeyCredential(properties.getApiKey()))
                .endpoint(properties.getEndpoint());
    }

    @Bean
    public AzureOpenAiChatOptions azureOpenAiChatOptions(AzureOpenAiProperties properties) {
        return AzureOpenAiChatOptions.builder()
                .deploymentName(properties.getDeploymentName())
                .temperature(properties.getTemperature())
                .topP(properties.getTopP())
                .maxTokens(properties.getMaxTokens())
                .build();
    }

    @Bean
    public ToolCallingManager toolCallingManager() {
        return DefaultToolCallingManager.builder().build();
    }

    @Bean
    public ChatModel chatModel(OpenAIClientBuilder clientBuilder,
                               AzureOpenAiChatOptions chatOptions,
                               ToolCallingManager toolCallingManager,
                               ObjectProvider<ObservationRegistry> observationRegistryProvider) {
        ObservationRegistry registry = observationRegistryProvider.getIfAvailable(ObservationRegistry::create);
        return new AzureOpenAiChatModel(clientBuilder, chatOptions, toolCallingManager, registry);
    }
}
