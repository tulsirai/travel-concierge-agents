package com.example.travelagent.config;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.KeyCredential;
import com.example.travelagent.tool.TravelInfoTool;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.aiservices.openai.chatcompletion.OpenAIChatCompletion;
import com.microsoft.semantickernel.plugin.KernelPlugin;
import com.microsoft.semantickernel.plugin.KernelPluginFactory;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
@EnableConfigurationProperties(SemanticKernelProperties.class)
public class SemanticKernelConfig {

    @Bean
    public OpenAIAsyncClient openAIAsyncClient(SemanticKernelProperties properties) {
        String apiKey = properties.getOpenai().getApiKey();
        if (!StringUtils.hasText(apiKey)) {
            throw new IllegalStateException("OPENAI_API_KEY is required to start the agent.");
        }
        return new OpenAIClientBuilder()
                .credential(new KeyCredential(apiKey))
                .endpoint(properties.getOpenai().getEndpoint())
                .buildAsyncClient();
    }

    @Bean
    public OpenAIChatCompletion openAIChatCompletion(OpenAIAsyncClient client,
                                                     SemanticKernelProperties properties) {
        return OpenAIChatCompletion.builder()
                .withModelId(properties.getDefaultModel())
                .withOpenAIAsyncClient(client)
                .build();
    }

    @Bean
    public KernelPlugin travelInfoPlugin(TravelInfoTool travelInfoTool) {
        return KernelPluginFactory.createFromObject(travelInfoTool, "travelTools");
    }

    @Bean
    public Kernel kernel(OpenAIChatCompletion chatCompletion, KernelPlugin travelInfoPlugin) {
        return Kernel.builder()
                .withAIService(ChatCompletionService.class, chatCompletion)
                .withPlugin(travelInfoPlugin)
                .build();
    }
}
