package kz.kaspi.lab.uploader.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "upload_requests",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_client_idempotency", columnNames = {"clientId", "idempotencyKey"})
        }
)
@Getter
@Setter
@NoArgsConstructor
public class UploadRequest {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String clientId;

    @Column(nullable = false)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UploadStatus status;

    @Column(length = 1024)
    private String errorMessage;

    @OneToOne
    @JoinColumn(name = "file_id")
    private FileRecord fileRecord;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

}
