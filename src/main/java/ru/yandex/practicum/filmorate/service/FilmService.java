package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.validation.FilmValidation;

import java.util.List;

@Service
public class FilmService {
    private final InMemoryFilmStorage storage;

    @Autowired
    public FilmService(InMemoryFilmStorage storage) {
        this.storage = storage;
    }

    public List<Film> findAllFilms() {
        return storage.findAllFilms();
    }

    public Film findFilmById(int filmId) {
        return storage.findFilmById(filmId);
    }

    public Film createFilm(Film film) {
        FilmValidation.validation(film);
        return storage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        FilmValidation.validation(film);
        storage.findFilmById(film.getId());

        return storage.updateFilm(film);
    }

    public Film addToLikes(long filmId, long userId) {
        storage.findFilmById(filmId);

        return storage.addToLikes(filmId, userId);
    }

    public Film deleteFromLikes(long filmId, long userId) {
        storage.findFilmById(filmId);

        return storage.deleteFromLikes(filmId, userId);
    }

    public List<Film> findPopularFilms(int count) {
        return storage.findPopularFilms(count);
    }
}
