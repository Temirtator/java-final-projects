package kz.kaspi.lab.uploader.model;

public enum UploadStatus {
    PENDING,
    COMPLETED,
    FAILED;

    public boolean isCompleted() {
        return this == COMPLETED;
    }
}
