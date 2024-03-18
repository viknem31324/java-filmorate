package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FilmStorage {
    List<Film> findAllFilms();
    Film findFilmById(int filmId);
    Film createFilm(Film requestFilm);
    Film updateFilm(Film film);
    void addToLikes(long filmId, long userId);
    void deleteFromLikes(long filmId, long userId);
    List<Film> findPopularFilms(int count);
}
