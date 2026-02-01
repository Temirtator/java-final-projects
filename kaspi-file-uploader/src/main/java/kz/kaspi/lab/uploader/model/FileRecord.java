package kz.kaspi.lab.uploader.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "file_records")
@Getter
@Setter
@NoArgsConstructor
public class FileRecord {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private long size;

    @Column(nullable = false, unique = true, length = 64)
    private String checksum;

    @Column(nullable = false)
    private String storagePath;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

}
