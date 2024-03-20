package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> findAllFilms();
    Film findFilmById(int filmId);
    Film createFilm(Film requestFilm);
    Film updateFilm(Film film);
    Film addToLikes(long filmId, long userId);
    Film deleteFromLikes(long filmId, long userId);
    List<Film> findPopularFilms(int count);
}
