package kz.kaspi.lab.moderation.service1.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.kaspi.lab.moderation.service1.config.RulesProperties;
import kz.kaspi.lab.moderation.service1.dto.AppealEvent;
import kz.kaspi.lab.moderation.service1.dto.EnrichmentResponse;
import kz.kaspi.lab.moderation.service1.dto.ModerationResultEvent;
import kz.kaspi.lab.moderation.service1.model.ActiveAppeal;
import kz.kaspi.lab.moderation.service1.model.ProcessedEvent;
import kz.kaspi.lab.moderation.service1.repository.ActiveAppealRepository;
import kz.kaspi.lab.moderation.service1.repository.ProcessedEventRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

@Service
public class ModerationService {

    private final ProcessedEventRepository processedEventRepository;
    private final ActiveAppealRepository activeAppealRepository;
    private final Service2Client service2Client;
    private final RulesProperties rulesProperties;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String outputTopic;

    public ModerationService(ProcessedEventRepository processedEventRepository,
                             ActiveAppealRepository activeAppealRepository,
                             Service2Client service2Client,
                             RulesProperties rulesProperties,
                             KafkaTemplate<String, String> kafkaTemplate,
                             ObjectMapper objectMapper,
                             @Value("${kafka.topic.output}") String outputTopic) {
        this.processedEventRepository = processedEventRepository;
        this.activeAppealRepository = activeAppealRepository;
        this.service2Client = service2Client;
        this.rulesProperties = rulesProperties;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.outputTopic = outputTopic;
    }

    @Transactional
    public void handleEvent(AppealEvent event) {
        if (event == null || event.getEventId() == null) {
            return;
        }

        try {
            ProcessedEvent processedEvent = new ProcessedEvent();
            processedEvent.setEventId(event.getEventId());
            processedEvent.setProcessedAt(OffsetDateTime.now());
            processedEventRepository.save(processedEvent);
        } catch (DataIntegrityViolationException ex) {
            return; // already processed
        }

        boolean hasActive = activeAppealRepository
                .findByClientIdAndCategoryAndActiveTrue(event.getClientId(), event.getCategory())
                .isPresent();
        if (hasActive) {
            return;
        }

        if (isOutsideWorkingHours(event)) {
            return;
        }

        EnrichmentResponse enrichment = new EnrichmentResponse();
        try {
            enrichment = service2Client.getEnrichment(event.getClientId(), event.getCategory());
        } catch (Exception ex) {
            return;
        }

        publishResult(event, enrichment);
        createActiveAppeal(event);
    }

    private boolean isOutsideWorkingHours(AppealEvent event) {
        if (event.getCategory() == null || !rulesProperties.getRestrictedCategories().contains(event.getCategory())) {
            return false;
        }
        ZonedDateTime now = ZonedDateTime.now(rulesProperties.getZoneId());
        return now.toLocalTime().isBefore(rulesProperties.getWorkStart())
                || now.toLocalTime().isAfter(rulesProperties.getWorkEnd());
    }

    private void publishResult(AppealEvent event, EnrichmentResponse enrichment) {
        ModerationResultEvent result = new ModerationResultEvent();
        result.setEventId(event.getEventId());
        result.setClientId(event.getClientId());
        result.setCategory(event.getCategory());
        result.setDecision("APPROVED");
        result.setProcessedAt(OffsetDateTime.now());

        try {
            String payload = objectMapper.writeValueAsString(result);
            kafkaTemplate.send(outputTopic, event.getEventId(), payload);
        } catch (Exception ignored) {
        }
    }

    private void createActiveAppeal(AppealEvent event) {
        ActiveAppeal activeAppeal = new ActiveAppeal();
        activeAppeal.setClientId(event.getClientId());
        activeAppeal.setCategory(event.getCategory());
        activeAppeal.setActive(true);
        activeAppeal.setCreatedAt(OffsetDateTime.now());
        try {
            activeAppealRepository.save(activeAppeal);
        } catch (DataIntegrityViolationException ignored) {
        }
    }

}
