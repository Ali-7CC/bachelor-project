package App.upload;

public class CouldNotParseException extends RuntimeException {
    public CouldNotParseException(String message) {
        super(message);
    }

    public CouldNotParseException(String message, Throwable cause) {
        super(message, cause);
    }


}
