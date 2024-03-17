package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.FilmValidation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private long filmId = 1;
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public List<Film> findAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film createFilm(Film requestFilm) {
        FilmValidation.validation(requestFilm);
        Film film = requestFilm.toBuilder()
                .id(filmId++)
                .build();

        log.debug("Текущий фильм: {}", film);

        films.put(film.getId(), film);

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        log.debug("Текущий фильм: {}", film);

        if (!films.containsKey(film.getId())) {
            throw new ValidationFilmException("Такого фильма нет!");
        }

        FilmValidation.validation(film);
        films.put(film.getId(), film);

        return film;
    }
}
