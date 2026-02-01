package kz.kaspi.lab.moderation.service2.controller;

import kz.kaspi.lab.moderation.service2.dto.EnrichmentResponse;
import kz.kaspi.lab.moderation.service2.service.EnrichmentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enrichment")
public class EnrichmentController {

    private final EnrichmentService enrichmentService;

    public EnrichmentController(EnrichmentService enrichmentService) {
        this.enrichmentService = enrichmentService;
    }

    @GetMapping("/{clientId}")
    public EnrichmentResponse getEnrichment(@PathVariable String clientId,
                                            @RequestParam String category) {
        return enrichmentService.getEnrichment(clientId, category);
    }
}
