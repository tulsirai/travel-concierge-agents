package com.example.travelagent.knowledge;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class PrivateKnowledgeService {

    private final List<PrivateFact> facts;

    public PrivateKnowledgeService(ObjectMapper mapper) {
        this.facts = loadFacts(mapper);
    }

    public Optional<String> findContext(String freeFormText) {
        if (!StringUtils.hasText(freeFormText)) {
            return Optional.empty();
        }
        String normalized = freeFormText.toLowerCase(Locale.ROOT);
        return facts.stream()
                .filter(fact -> fact.getKeywords().stream().anyMatch(normalized::contains))
                .map(PrivateFact::getSummary)
                .findFirst();
    }

    private List<PrivateFact> loadFacts(ObjectMapper mapper) {
        Resource resource = new ClassPathResource("data/private_knowledge.json");
        if (!resource.exists()) {
            log.warn("Private knowledge file not found; continuing without private context.");
            return Collections.emptyList();
        }
        try (InputStream inputStream = resource.getInputStream()) {
            return mapper.readValue(inputStream, new TypeReference<>() {});
        } catch (IOException ex) {
            log.error("Failed to load private knowledge entries", ex);
            return Collections.emptyList();
        }
    }

    @Data
    public static class PrivateFact {
        private String name;
        private List<String> keywords = List.of();
        private String summary;
    }
}
