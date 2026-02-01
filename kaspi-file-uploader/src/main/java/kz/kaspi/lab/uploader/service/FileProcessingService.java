package kz.kaspi.lab.uploader.service;

import kz.kaspi.lab.uploader.model.FileRecord;
import kz.kaspi.lab.uploader.model.UploadRequest;
import kz.kaspi.lab.uploader.model.UploadStatus;
import kz.kaspi.lab.uploader.repository.FileRecordRepository;
import kz.kaspi.lab.uploader.repository.UploadRequestRepository;
import kz.kaspi.lab.uploader.storage.FileStorageService;
import kz.kaspi.lab.uploader.storage.StoredFile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileProcessingService {

    private final UploadRequestRepository uploadRequestRepository;
    private final FileRecordRepository fileRecordRepository;
    private final FileStorageService fileStorageService;

    public FileProcessingService(UploadRequestRepository uploadRequestRepository,
                                 FileRecordRepository fileRecordRepository,
                                 FileStorageService fileStorageService) {
        this.uploadRequestRepository = uploadRequestRepository;
        this.fileRecordRepository = fileRecordRepository;
        this.fileStorageService = fileStorageService;
    }

    @Async("fileProcessingExecutor")
    public void processAsync(UUID requestId,
                             Path tempFile,
                             String originalFilename,
                             String contentType,
                             long size) {
        String storedPath = null;
        try {
            StoredFile storedFile = fileStorageService.store(tempFile);
            storedPath = storedFile.storagePath();

            FileRecord fileRecord = resolveFileRecord(storedFile, originalFilename, contentType, size, storedPath);
            markCompleted(requestId, fileRecord);
        } catch (Exception ex) {
            fileStorageService.deleteIfExists(storedPath);
            markFailed(requestId, ex.getMessage());
        } finally {
            try {
                Files.deleteIfExists(tempFile);
            } catch (IOException ignored) {
            }
        }
    }

    private FileRecord resolveFileRecord(StoredFile storedFile,
                                         String originalFilename,
                                         String contentType,
                                         long size,
                                         String storedPath) {
        Optional<FileRecord> existing = fileRecordRepository.findByChecksum(storedFile.checksum());
        if (existing.isPresent()) {
            fileStorageService.deleteIfExists(storedPath);
            return existing.get();
        }

        FileRecord record = new FileRecord();
        record.setId(UUID.randomUUID());
        record.setOriginalFilename(originalFilename == null ? "unknown" : originalFilename);
        record.setContentType(contentType == null ? "application/octet-stream" : contentType);
        record.setSize(size);
        record.setChecksum(storedFile.checksum());
        record.setStoragePath(storedFile.storagePath());
        record.setCreatedAt(OffsetDateTime.now());

        try {
            return fileRecordRepository.save(record);
        } catch (DataIntegrityViolationException ex) {
            fileStorageService.deleteIfExists(storedPath);
            return fileRecordRepository.findByChecksum(storedFile.checksum())
                    .orElseThrow(() -> ex);
        }
    }

    @Transactional
    public void markCompleted(UUID requestId, FileRecord fileRecord) {
        UploadRequest request = uploadRequestRepository.findById(requestId).orElseThrow();
        request.setStatus(UploadStatus.COMPLETED);
        request.setFileRecord(fileRecord);
        request.setUpdatedAt(OffsetDateTime.now());
        uploadRequestRepository.save(request);
    }

    @Transactional
    public void markFailed(UUID requestId, String errorMessage) {
        uploadRequestRepository.findById(requestId).ifPresent(request -> {
            request.setStatus(UploadStatus.FAILED);
            request.setErrorMessage(errorMessage == null ? "Unknown error" : errorMessage);
            request.setUpdatedAt(OffsetDateTime.now());
            uploadRequestRepository.save(request);
        });
    }
}
