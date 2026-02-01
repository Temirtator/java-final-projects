package kz.kaspi.lab.uploader.controller;

import kz.kaspi.lab.uploader.dto.UploadResponse;
import kz.kaspi.lab.uploader.dto.UploadStatusResponse;
import kz.kaspi.lab.uploader.model.UploadRequest;
import kz.kaspi.lab.uploader.repository.UploadRequestRepository;
import kz.kaspi.lab.uploader.service.UploadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/api/uploads")
public class UploadController {

    private static final long MAX_FILE_SIZE_BYTES = 50L * 1024 * 1024;

    private final UploadService uploadService;
    private final UploadRequestRepository uploadRequestRepository;

    public UploadController(UploadService uploadService, UploadRequestRepository uploadRequestRepository) {
        this.uploadService = uploadService;
        this.uploadRequestRepository = uploadRequestRepository;
    }

    @PostMapping
    public ResponseEntity<UploadResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("X-Client-Id") String clientId,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    ) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new UploadResponse(null, null, null, null, "File is empty"));
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            return ResponseEntity.badRequest()
                .body(new UploadResponse(null, null, null, null, "File size exceeds 50 MB"));
        }

        String validationError = validateFile(file);
        if (validationError != null) {
            return ResponseEntity.badRequest()
                .body(new UploadResponse(null, null, null, null, validationError));
        }

        UploadResponse response = uploadService.handleUpload(file, clientId, idempotencyKey);
        if (response.getStatus() != null && response.getStatus().isCompleted()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UploadStatusResponse> status(@PathVariable("id") UUID requestId) {
        return uploadRequestRepository.findById(requestId)
                .map(this::toStatusResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private UploadStatusResponse toStatusResponse(UploadRequest request) {
        UploadStatusResponse response = new UploadStatusResponse();
        response.setRequestId(request.getId());
        response.setStatus(request.getStatus());
        if (request.getFileRecord() != null) {
            response.setFileId(request.getFileRecord().getId());
            response.setChecksum(request.getFileRecord().getChecksum());
        }
        response.setErrorMessage(request.getErrorMessage());
        response.setUpdatedAt(request.getUpdatedAt());
        return response;
    }

    private String validateFile(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        String extension = originalName == null ? "" : originalName.substring(originalName.lastIndexOf('.') + 1)
                .toLowerCase(Locale.ROOT);

        boolean allowedExtension = extension.equals("txt")
                || extension.equals("doc")
                || extension.equals("docx")
                || extension.equals("jpg")
                || extension.equals("jpeg")
                || extension.equals("png")
                || extension.equals("gif")
                || extension.equals("bmp");

        String contentType = file.getContentType();
        boolean allowedContentType = contentType != null && (
                contentType.startsWith("image/")
                        || contentType.equals("text/plain")
                        || contentType.equals("application/msword")
                        || contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
        );

        if (!allowedExtension || !allowedContentType) {
            return "Invalid file type. Allowed: images (.jpg, .jpeg, .png, .gif, .bmp), .txt, .doc, .docx";
        }

        return null;
    }
}
