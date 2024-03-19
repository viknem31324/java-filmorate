package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataIncorrectException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.FilmValidation;

import java.util.*;
import java.util.stream.Collectors;

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
    public Film findFilmById(int filmId) {
        log.info("id фильма: {}", filmId);

        return films.values().stream()
                .filter(film -> film.getId() == filmId)
                .findFirst()
                .orElseThrow(() -> new FilmNotFoundException(String.format("Фильм с id %d не найден", filmId)));
    }

    @Override
    public List<Film> findPopularFilms(int count) {
        log.info("Лимит фильмов: {}", count);

        return films.values().stream()
                .sorted(Comparator.comparingInt(film -> ((Film) film).getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public Film createFilm(Film requestFilm) {
        FilmValidation.validation(requestFilm);
        Film film = requestFilm.toBuilder()
                .id(filmId++)
                .likes(Set.of())
                .build();

        log.info("Текущий фильм: {}", film);

        films.put(film.getId(), film);

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        log.info("Текущий фильм: {}", film);

        if (!films.containsKey(film.getId())) {
            throw new FilmNotFoundException("Такого фильма нет!");
        }

        FilmValidation.validation(film);
        films.put(film.getId(), film);

        return film;
    }

    @Override
    public Film addToLikes(long filmId, long userId) {
        if (!films.containsKey(filmId)) {
            throw new FilmNotFoundException(String.format("Фильм с id %d не найден", filmId));
        }

        log.info("Текущий фильм: {}", films.get(filmId));
        log.info("id пользователя ставящего лайк: {}", userId);

        Film film = films.get(filmId);
        film.getLikes().add(userId);
        return film;
    }

    @Override
    public Film deleteFromLikes(long filmId, long userId) {
        if (!films.containsKey(filmId)) {
            throw new FilmNotFoundException(String.format("Фильм с id %d не найден", filmId));
        }

        log.info("Текущий фильм: {}", films.get(filmId));
        log.info("id пользователя убирающего лайк: {}", userId);

        Film film = films.get(filmId);
        film.getLikes().remove(userId);
        return film;
    }
}
