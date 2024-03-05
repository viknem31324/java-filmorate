package ru.yandex.practicum.filmorate.exception;

public class ValidationFilmException extends RuntimeException {
    public ValidationFilmException(final String message) {
        super(message);
    }
}
