package kz.kaspi.lab.moderation.service2.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisSeeder implements CommandLineRunner {

    private final HashOperations<String, String, String> hashOperations;

    public RedisSeeder(StringRedisTemplate redisTemplate) {
        this.hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public void run(String... args) {
        String key = "enrichment:client-1:fraud";
        hashOperations.put(key, "customerType", "VIP");
        hashOperations.put(key, "riskLevel", "HIGH");
    }
}
