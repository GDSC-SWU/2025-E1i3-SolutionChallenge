package me.hakyuwon.sweetCheck.service;

import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@Slf4j
public class AnalyzeService {
    private final WebClient webClient;

    @Value("${ai.api.url}")
    private String fastApiUrl;

    public AnalyzeService(WebClient.Builder builder,
                          @Value("${ai.api.url}") String fastApiUrl) {
        this.webClient = builder.baseUrl(fastApiUrl).build();
    }

    public Mono<Map<String, Object>> analyzeDay(Map<String, Resource> images) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        images.forEach((key, resource) -> builder.part(key, resource));

        return webClient.post()
                .uri("")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .onStatus(response -> response.isError(), res ->
                        res.bodyToMono(String.class).flatMap(body -> {
                            log.error("FastAPI error: {}", body);
                            return Mono.error(new RuntimeException("FastAPI server error: " + body));
                        })
                )
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
    }
}

