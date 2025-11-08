package com.example.travelagent.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "spring.ai.azure.openai")
public class AzureOpenAiProperties {

    @NotBlank
    private String apiKey;

    @NotBlank
    private String endpoint;

    @NotBlank
    private String deploymentName;

    @NotNull
    private Double temperature = 0.4;

    @NotNull
    private Double topP = 0.9;

    @NotNull
    private Integer maxTokens = 800;
}
