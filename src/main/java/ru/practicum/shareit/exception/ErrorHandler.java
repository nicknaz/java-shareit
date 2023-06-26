package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ValidationException;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(NotFoundedException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> objectNotFoundedException(final NotFoundedException e) {
        log.debug("Получен статус 404 Not found {}", e.getMessage(), e);
        return Map.of("exception", e.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> emailException(final EmailAlreadyExistException e) {
        log.debug("Получен статус 409 Conflict {}", e.getMessage(), e);
        return Map.of("exception", e.getMessage());
    }

    @ExceptionHandler(ConflictIdException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> conflictIdException(final ConflictIdException e) {
        log.debug("Получен статус 409 Conflict {}", e.getMessage(), e);
        return Map.of("exception", e.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleException(final Exception e) {
        log.debug("Получен статус 400 Bad Request {}", e.getMessage(), e);
        return Map.of("exception", e.getMessage());
    }

    @ExceptionHandler(InvalidDateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> dateException(final Exception e) {
        log.debug("Получен статус 400 Bad Request {}", e.getMessage(), e);
        return Map.of("exception", e.getMessage());
    }

    @ExceptionHandler(StatusUnsuportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> statusException(final Exception e) {
        log.debug("Получен статус 400 Bad Request {}", e.getMessage(), e);
        return Map.of("error", "Unknown state: UNSUPPORTED_STATUS");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> statusUnsupportedException(final Exception e) {
        log.debug("Получен статус 400 Bad Request {}", e.getMessage(), e);
        return Map.of("error", "Unknown state: UNSUPPORTED_STATUS");
    }

    @ExceptionHandler(AccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> accessException(final AccessException e) {
        log.debug("Получен статус 403 FORBIDDEN {}", e.getMessage(), e);
        return Map.of("exception", e.getMessage());
    }
}
