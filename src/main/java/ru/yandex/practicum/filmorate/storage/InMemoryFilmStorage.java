package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private long filmId = 1;
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public List<Film> findAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film findFilmById(long filmId) {
        log.debug("id фильма: {}", filmId);

        return films.values().stream()
                .filter(film -> film.getId() == filmId)
                .findFirst()
                .orElseThrow(() -> new FilmNotFoundException(String.format("Фильм с id %d не найден", filmId)));
    }

    @Override
    public List<Film> findPopularFilms(int count) {
        log.debug("Лимит фильмов: {}", count);

        return films.values().stream()
                .sorted(Comparator.comparingInt(item -> {
                    Film film = (Film) item;
                    Set<Long> likes = film.getLikes();
                    if (likes != null) {
                        return likes.size();
                    }
                    return 0;
                }).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public Film createFilm(Film requestFilm) {
        Film film = requestFilm.toBuilder()
                .id(filmId++)
                .likes(requestFilm.getLikes() == null ? new HashSet<>() : requestFilm.getLikes())
                .build();

        log.debug("Текущий фильм: {}", film);

        films.put(film.getId(), film);

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        log.debug("Текущий фильм: {}", film);

        films.put(film.getId(), film);

        return film;
    }

    @Override
    public Film addToLikes(long filmId, long userId) {
        log.debug("Текущий фильм: {}", films.get(filmId));
        log.debug("id пользователя ставящего лайк: {}", userId);

        Set<Long> likes = new HashSet<>(films.get(filmId).getLikes());
        likes.add(userId);

        Film film = films.get(filmId).toBuilder()
                .likes(likes)
                .build();

        films.put(film.getId(), film);

        return film;
    }

    @Override
    public Film deleteFromLikes(long filmId, long userId) {
        log.debug("Текущий фильм: {}", films.get(filmId));
        log.debug("id пользователя убирающего лайк: {}", userId);

        Set<Long> likes = new HashSet<>(films.get(filmId).getLikes());
        likes.remove(userId);

        Film film = films.get(filmId).toBuilder()
                .likes(likes)
                .build();

        films.put(film.getId(), film);

        return film;
    }
}
