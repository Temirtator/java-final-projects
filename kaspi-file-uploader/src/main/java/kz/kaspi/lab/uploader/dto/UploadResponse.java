package kz.kaspi.lab.uploader.dto;

import kz.kaspi.lab.uploader.model.UploadStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadResponse {
    private UUID requestId;
    private UploadStatus status;
    private UUID fileId;
    private String checksum;
    private String message;
}
