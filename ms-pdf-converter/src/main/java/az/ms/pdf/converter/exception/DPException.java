package az.ms.pdf.converter.exception;

import lombok.Getter;
import java.util.UUID;

@Getter
public class DPException extends RuntimeException {

    private final String errorUuid;

    private final String errorCode;

    private final String errorMessage;

    public DPException(String errorUuid, String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorUuid = errorUuid;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public DPException(String errorCode, String errorMessage) {
        this(UUID.randomUUID().toString(), errorCode, errorMessage);
    }

    public DPException(String errorMessage) {
        this(UUID.randomUUID().toString(), null, errorMessage);
    }
}