package kz.kaspi.lab.moderation.service2.service;

import kz.kaspi.lab.moderation.service2.dto.EnrichmentResponse;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class EnrichmentService {

    private static final String KEY_PREFIX = "enrichment:";
    private final HashOperations<String, String, String> hashOperations;

    public EnrichmentService(StringRedisTemplate redisTemplate) {
        this.hashOperations = redisTemplate.opsForHash();
    }

    public EnrichmentResponse getEnrichment(String clientId, String category) {
        String key = KEY_PREFIX + clientId + ":" + category;
        String customerType = hashOperations.get(key, "customerType");
        String riskLevel = hashOperations.get(key, "riskLevel");

        EnrichmentResponse response = new EnrichmentResponse();
        if (customerType == null && riskLevel == null) {
            response.setFound(false);
            return response;
        }
        response.setFound(true);
        response.setCustomerType(customerType);
        response.setRiskLevel(riskLevel);
        return response;
    }
}
