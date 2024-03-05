package ru.yandex.practicum.filmorate.validation;

import ru.yandex.practicum.filmorate.exception.ValidationFilmException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;

public class FilmValidation {
    private static final Integer MAX_LENGTH_DESCRIPTION = 200;
    private static final LocalDate BIRTHDAY_MOVIE = LocalDate.of(1895, Month.DECEMBER, 28);
    public static void validation(Film film) throws ValidationFilmException {
        if (film.getDescription().length() > MAX_LENGTH_DESCRIPTION) {
            throw new ValidationFilmException("Описание превышает 200 символов!");
        }

        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(BIRTHDAY_MOVIE)) {
            throw new ValidationFilmException("Неверная дата выхода фильма в прокат!");
        }

        if (film.getDuration() != null && film.getDuration().isNegative()) {
            throw new ValidationFilmException("Неверная продолжительность фильма!");
        }
    }
}
