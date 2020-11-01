package App.Storage;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response structure for POST requests on /upload
 */
public class FileStorageResponse {
    private final List<String> attributeKeys;
    private final String errorMessage;
    private final LocalDateTime localDateTime;

    public FileStorageResponse(List<String> attributeKeys, String errorMessage, LocalDateTime localDateTime) {
        this.attributeKeys = attributeKeys;
        this.errorMessage = errorMessage;
        this.localDateTime = localDateTime;
    }

    public List<String> getAttributeKeys() {
        return attributeKeys;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }
}
