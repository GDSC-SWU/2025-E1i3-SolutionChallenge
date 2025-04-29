package me.hakyuwon.sweetCheck.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GeminiService {
    private final WebClient webClient;
    private final String apiKey = System.getenv("GEMINI_API_KEY");

    public GeminiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent")
                .build();
    }

    public String recommendMenu(String prompt) {
        String requestBody = buildRequestBody(prompt);

        return webClient.post()
                .uri(uriBuilder -> uriBuilder.queryParam("key", apiKey).build())
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    private String buildRequestBody(String prompt) {
        return """
        {
            "contents": [{
                "parts": [{
                    "text": "%s"
                }]
            }]
        }
        """.formatted(prompt);
    }
}
