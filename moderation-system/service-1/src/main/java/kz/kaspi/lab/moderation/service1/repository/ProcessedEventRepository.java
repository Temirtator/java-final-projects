package kz.kaspi.lab.moderation.service1.repository;

import kz.kaspi.lab.moderation.service1.model.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, String> {
}
