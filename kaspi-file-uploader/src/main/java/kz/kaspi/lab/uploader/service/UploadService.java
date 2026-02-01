package kz.kaspi.lab.uploader.service;

import kz.kaspi.lab.uploader.config.StorageProperties;
import kz.kaspi.lab.uploader.dto.UploadResponse;
import kz.kaspi.lab.uploader.model.UploadRequest;
import kz.kaspi.lab.uploader.model.UploadStatus;
import kz.kaspi.lab.uploader.repository.UploadRequestRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class UploadService {

    private final UploadRequestRepository uploadRequestRepository;
    private final FileProcessingService fileProcessingService;
    private final StorageProperties storageProperties;

    public UploadService(UploadRequestRepository uploadRequestRepository,
                         FileProcessingService fileProcessingService,
                         StorageProperties storageProperties) {
        this.uploadRequestRepository = uploadRequestRepository;
        this.fileProcessingService = fileProcessingService;
        this.storageProperties = storageProperties;
    }

    public UploadResponse handleUpload(MultipartFile file, String clientId, String idempotencyKey) throws IOException {
        UploadRequest existing = uploadRequestRepository
                .findByClientIdAndIdempotencyKey(clientId, idempotencyKey)
                .orElse(null);

        if (existing != null) {
            return buildResponse(existing, "Idempotent response");
        }

        UploadRequest request = new UploadRequest();
        request.setId(UUID.randomUUID());
        request.setClientId(clientId);
        request.setIdempotencyKey(idempotencyKey);
        request.setStatus(UploadStatus.PENDING);
        request.setCreatedAt(OffsetDateTime.now());
        request.setUpdatedAt(OffsetDateTime.now());

        try {
            request = uploadRequestRepository.save(request);
        } catch (DataIntegrityViolationException ex) {
            UploadRequest existingRequest = uploadRequestRepository
                    .findByClientIdAndIdempotencyKey(clientId, idempotencyKey)
                    .orElseThrow();
            return buildResponse(existingRequest, "Idempotent response");
        }

        try {
            Path tempDir = Path.of(storageProperties.getTempPath());
            Files.createDirectories(tempDir);
            Path tempFile = tempDir.resolve(UUID.randomUUID() + ".tmp");
            file.transferTo(tempFile);

            fileProcessingService.processAsync(
                request.getId(),
                tempFile,
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize()
            );
        } catch (IOException ex) {
            request.setStatus(UploadStatus.FAILED);
            request.setErrorMessage(ex.getMessage());
            request.setUpdatedAt(OffsetDateTime.now());
            uploadRequestRepository.save(request);
            return new UploadResponse(request.getId(), request.getStatus(), null, null, "Upload failed");
        }

        return new UploadResponse(request.getId(), request.getStatus(), null, null, "Upload accepted");
    }

    private UploadResponse buildResponse(UploadRequest request, String message) {
        if (request.getFileRecord() != null) {
            return new UploadResponse(
                    request.getId(),
                    request.getStatus(),
                    request.getFileRecord().getId(),
                    request.getFileRecord().getChecksum(),
                    message
            );
        }
        return new UploadResponse(request.getId(), request.getStatus(), null, null, message);
    }
}
