package kz.kaspi.lab.uploader.storage;

import kz.kaspi.lab.uploader.config.StorageProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class FileStorageService {

    private final StorageProperties storageProperties;

    public FileStorageService(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    public StoredFile store(Path tempFile) throws IOException {
        Path baseDir = Path.of(storageProperties.getBasePath());
        Files.createDirectories(baseDir);

        String targetName = UUID.randomUUID().toString();
        Path targetPath = baseDir.resolve(targetName);

        MessageDigest digest = sha256();
        try (InputStream inputStream = Files.newInputStream(tempFile, StandardOpenOption.READ);
            OutputStream outputStream = Files.newOutputStream(targetPath, StandardOpenOption.CREATE_NEW)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
                digest.update(buffer, 0, read);
            }
        }

        String checksum = HexFormat.of().formatHex(digest.digest());
        return new StoredFile(targetPath.toString(), checksum);
    }

    public void deleteIfExists(String storagePath) {
        if (storagePath == null) {
            return;
        }
        try {
            Files.deleteIfExists(Path.of(storagePath));
        } catch (IOException ignored) {
        }
    }

    private MessageDigest sha256() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
