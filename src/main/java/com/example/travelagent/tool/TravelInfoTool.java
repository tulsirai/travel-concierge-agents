package com.example.travelagent.tool;

import com.microsoft.semantickernel.semanticfunctions.annotations.DefineKernelFunction;
import com.microsoft.semantickernel.semanticfunctions.annotations.KernelFunctionParameter;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class TravelInfoTool {

    private static final Map<String, String> QUICK_FACTS = Map.of(
            "paris", "Peak season is spring and early autumn. Louvre tickets should be booked 2 weeks out.",
            "tokyo", "Best visited March-May or October-November. Reserve JR Pass and pocket Wi-Fi ahead.",
            "new york", "Summer is busy; September offers good weather. Broadway tickets sell out quickly."
    );

    @DefineKernelFunction(
            name = "destination_facts",
            description = "Returns planning insights for a given destination.")
    public String destinationFacts(
            @KernelFunctionParameter(name = "city", description = "Destination city") String city) {
        if (city == null || city.isBlank()) {
            return "No city provided.";
        }
        return Optional.ofNullable(QUICK_FACTS.get(city.toLowerCase()))
                .orElse("Research visas, ideal seasons, and must-book activities before you travel to " + city + ".");
    }
}
