package ru.yandex.practicum.filmorate.exception;

public class ParamsIncorrectException extends RuntimeException {
    public ParamsIncorrectException(final String message) {
        super(message);
    }
}
