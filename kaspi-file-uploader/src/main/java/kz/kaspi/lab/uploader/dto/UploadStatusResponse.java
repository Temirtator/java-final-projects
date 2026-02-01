package kz.kaspi.lab.uploader.dto;

import kz.kaspi.lab.uploader.model.UploadStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class UploadStatusResponse {
    private UUID requestId;
    private UploadStatus status;
    private UUID fileId;
    private String checksum;
    private String errorMessage;
    private OffsetDateTime updatedAt;
}
