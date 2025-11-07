package com.example.travelagent.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "semantic-kernel")
public class SemanticKernelProperties {

    /**
     * The default chat completion model, e.g. gpt-4o-mini.
     */
    @NotBlank
    private String defaultModel;

    private final OpenAiProperties openai = new OpenAiProperties();

    @Getter
    @Setter
    public static class OpenAiProperties {
        /**
         * OpenAI API endpoint. Defaults to the public endpoint.
         */
        private String endpoint = "https://api.openai.com/v1";

        /**
         * API key or Azure AD token, injected via env var.
         */
        private String apiKey;
    }
}
