package kz.kaspi.lab.uploader.repository;

import kz.kaspi.lab.uploader.model.UploadRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UploadRequestRepository extends JpaRepository<UploadRequest, UUID> {
    Optional<UploadRequest> findByClientIdAndIdempotencyKey(String clientId, String idempotencyKey);
}
