package App.upload;

public class CouldNotLoadFileException extends RuntimeException {
    public CouldNotLoadFileException(String message) {
        super(message);
    }

    public CouldNotLoadFileException(String message, Throwable cause) {
        super(message, cause);
    }


}
