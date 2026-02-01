package kz.kaspi.lab.moderation.service1.service;

import kz.kaspi.lab.moderation.service1.dto.EnrichmentResponse;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class Service2Client {

    private final WebClient webClient;

    public Service2Client(WebClient webClient) {
        this.webClient = webClient;
    }

    @Retryable(
            retryFor = {WebClientResponseException.class, RuntimeException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 300, multiplier = 2)
    )
    public EnrichmentResponse getEnrichment(String clientId, String category) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/enrichment/{clientId}")
                        .queryParam("category", category)
                        .build(clientId))
                .retrieve()
                .bodyToMono(EnrichmentResponse.class)
                .block();
    }
}
