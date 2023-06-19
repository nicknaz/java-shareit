package ru.practicum.shareit.exception;

public class ConflictIdException extends RuntimeException {
    public ConflictIdException() {
    }

    public ConflictIdException(String message) {
        super(message);
    }

    public ConflictIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConflictIdException(Throwable cause) {
        super(cause);
    }
}
