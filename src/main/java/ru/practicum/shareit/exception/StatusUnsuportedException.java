package ru.practicum.shareit.exception;

public class StatusUnsuportedException extends RuntimeException {
    public StatusUnsuportedException() {
    }

    public StatusUnsuportedException(String message) {
        super(message);
    }

    public StatusUnsuportedException(String message, Throwable cause) {
        super(message, cause);
    }

    public StatusUnsuportedException(Throwable cause) {
        super(cause);
    }
}
