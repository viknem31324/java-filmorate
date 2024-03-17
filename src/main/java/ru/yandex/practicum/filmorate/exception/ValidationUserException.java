package ru.yandex.practicum.filmorate.exception;

public class ValidationUserException extends RuntimeException {
    public ValidationUserException(final String message) {
        super(message);
    }
}
