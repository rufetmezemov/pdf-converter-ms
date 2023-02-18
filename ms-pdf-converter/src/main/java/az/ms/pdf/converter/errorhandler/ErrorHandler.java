package az.ms.pdf.converter.errorhandler;

import az.ms.pdf.converter.exception.FileException;
import az.ms.pdf.converter.logger.DPLogger;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

    private static final DPLogger log = DPLogger.getLogger(ErrorHandler.class);

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({IOException.class, FileException.class})
    public ErrorResponse handleServerException(Exception ex) {
        log.error("ActionLog.handleServerException.error: {} {}", ex.getClass().getSimpleName(), ex.getMessage());
        return new ErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.name());
    }
}