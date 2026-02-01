package kz.kaspi.lab.uploader.repository;

import kz.kaspi.lab.uploader.model.FileRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FileRecordRepository extends JpaRepository<FileRecord, UUID> {
    Optional<FileRecord> findByChecksum(String checksum);
}
