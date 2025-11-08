package com.example.travelagent.tool;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class TravelInfoTool {

    private static final Map<String, String> QUICK_FACTS = Map.of(
            "paris", "Peak season is spring and early autumn. Louvre tickets should be booked 2 weeks out.",
            "tokyo", "Best visited March-May or October-November. Reserve JR Pass and pocket Wi-Fi ahead.",
            "new york", "Summer is busy; September offers good weather. Broadway tickets sell out quickly."
    );

    public String destinationFacts(String city) {
        if (!StringUtils.hasText(city)) {
            return "No city provided.";
        }
        return QUICK_FACTS.getOrDefault(city.toLowerCase(Locale.ROOT),
                "Research visas, ideal seasons, and must-book activities before you travel to " + city + ".");
    }

    public Optional<DestinationFact> detectFact(String text) {
        if (!StringUtils.hasText(text)) {
            return Optional.empty();
        }
        String normalized = text.toLowerCase(Locale.ROOT);
        return QUICK_FACTS.keySet().stream()
                .filter(normalized::contains)
                .findFirst()
                .map(city -> new DestinationFact(city, destinationFacts(city)));
    }

    public record DestinationFact(String city, String advice) {
    }
}
