package ru.yandex.practicum.filmorate.exception;

public class DataIncorrectException extends RuntimeException {
    public DataIncorrectException(final String message) {
        super(message);
    }
}
