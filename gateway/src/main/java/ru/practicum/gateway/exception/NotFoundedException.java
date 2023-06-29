package ru.practicum.gateway.exception;

public class NotFoundedException extends RuntimeException {

    public NotFoundedException(String message) {
        super(message);
    }

}
