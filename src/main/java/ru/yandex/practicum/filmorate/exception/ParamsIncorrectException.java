package ru.yandex.practicum.filmorate.exception;

public class ParamsIncorrectException extends RuntimeException {
    private final String parameter;

    public ParamsIncorrectException(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return parameter;
    }
}
