package ru.practicum.server.exception;

public class NotFoundedException extends RuntimeException {

    public NotFoundedException(String message) {
        super(message);
    }

}
