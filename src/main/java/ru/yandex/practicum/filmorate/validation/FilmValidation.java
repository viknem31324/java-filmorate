package ru.yandex.practicum.filmorate.validation;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationFilmException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;

@Slf4j
public class FilmValidation {
    private static final Integer MAX_LENGTH_DESCRIPTION = 200;
    private static final LocalDate BIRTHDAY_MOVIE = LocalDate.of(1895, Month.DECEMBER, 28);

    public static void validation(Film film) throws ValidationFilmException {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationFilmException("Название фильма не может быть путым!");
        }

        if (film.getDescription() != null && film.getDescription().length() > MAX_LENGTH_DESCRIPTION) {
            log.debug("Текущее описание фильма: {}", film.getDescription().length());
            throw new ValidationFilmException("Описание превышает 200 символов!");
        }

        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(BIRTHDAY_MOVIE)) {
            log.debug("Текущая дата выхода фильма в прокат: {}", film.getReleaseDate());
            throw new ValidationFilmException("Неверная дата выхода фильма в прокат!");
        }

        if (film.getDuration() != null && film.getDuration() < 1) {
            log.debug("Текущая продолжительность фильма: {}", film.getDuration());
            throw new ValidationFilmException("Неверная продолжительность фильма!");
        }
    }
}
