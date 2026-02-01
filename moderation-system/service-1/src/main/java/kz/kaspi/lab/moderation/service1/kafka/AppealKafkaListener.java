package kz.kaspi.lab.moderation.service1.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.kaspi.lab.moderation.service1.dto.AppealEvent;
import kz.kaspi.lab.moderation.service1.service.ModerationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AppealKafkaListener {

    private final ModerationService moderationService;
    private final ObjectMapper objectMapper;

    public AppealKafkaListener(ModerationService moderationService, ObjectMapper objectMapper) {
        this.moderationService = moderationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${kafka.topic.input}", groupId = "service-1")
    public void onMessage(String payload) throws Exception {
        AppealEvent event = objectMapper.readValue(payload, AppealEvent.class);
        moderationService.handleEvent(event);
    }
}
