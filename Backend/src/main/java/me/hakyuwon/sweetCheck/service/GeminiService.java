package me.hakyuwon.sweetCheck.service;

import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GeminiService {
    private final WebClient webClient;
    private final String apiKey = System.getenv("GEMINI_API_KEY");

    public GeminiService(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .build();
    }

    public String recommendMenu(String prompt) {
        String body = """
        {
          "contents": [{
            "parts": [
              { "text": "%s" }
            ]
          }]
        }
        """.formatted(prompt);

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/models/gemini-2.0-flash:generateContent")
                        .queryParam("key", apiKey)
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}

