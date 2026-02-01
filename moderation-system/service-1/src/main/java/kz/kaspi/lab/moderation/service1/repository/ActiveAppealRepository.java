package kz.kaspi.lab.moderation.service1.repository;

import kz.kaspi.lab.moderation.service1.model.ActiveAppeal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActiveAppealRepository extends JpaRepository<ActiveAppeal, Long> {
    Optional<ActiveAppeal> findByClientIdAndCategoryAndActiveTrue(String clientId, String category);
}
