package ru.yandex.practicum.filmorate.exception;

public class MpaNotFoundException extends RuntimeException {
    public MpaNotFoundException(final String message) {
        super(message);
    }
}