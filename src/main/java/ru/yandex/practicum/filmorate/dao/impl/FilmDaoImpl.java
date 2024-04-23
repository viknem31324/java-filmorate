package ru.yandex.practicum.filmorate.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ParamsIncorrectException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FilmDaoImpl implements FilmDao {
    private final Logger log = LoggerFactory.getLogger(FilmDaoImpl.class);

    private final JdbcTemplate jdbcTemplate;
    private final MpaDao mpaDao;
    private final GenreDao genreDao;

    @Autowired
    public FilmDaoImpl(JdbcTemplate jdbcTemplate, MpaDao mpaDao, GenreDao genreDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDao = mpaDao;
        this.genreDao = genreDao;
    }

    @Override
    public List<Film> findAllFilms() {
        String sql = "select f.id, f.name, f.description, f.release_date, f.duration, " +
                "count(l.film_id) as rate, f.mpa_id " +
                "from films as f " +
                "left join likes as l on f.id = l.film_id " +
                "group by f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id;";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film findFilmById(long id) {
        String sql = "select f.id, f.name, f.description, f.release_date, f.duration, " +
                "count(l.film_id) as rate, f.mpa_id " +
                "from films as f " +
                "left join likes as l on f.id = l.film_id " +
                "where f.id = ? " +
                "group by f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id;";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql, id);

        if (filmRows.next()) {
            List<Genre> genres = getGenresList(id);
            long mpaId = filmRows.getLong("mpa_id");
            Mpa mpa = mpaDao.findMpaById(mpaId);

            Film film = Film.builder()
                    .id(filmRows.getLong("id"))
                    .name(filmRows.getString("name"))
                    .description(filmRows.getString("description"))
                    .releaseDate(filmRows.getDate("release_date").toLocalDate())
                    .duration(filmRows.getInt("duration"))
                    .rate(filmRows.getInt("rate"))
                    .genres(genres)
                    .mpa(mpa)
                    .build();

            log.info("Найден фильм: {} {}", film.getId(), film.getName());

            return film;
        } else {
            log.info("Фильм с идентификатором {} не найден.", id);
            throw new FilmNotFoundException(String.format("Фильм с id %d не найден", id));
        }
    }

    @Override
    public Film createFilm(Film requestFilm) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", requestFilm.getName());
        parameters.put("description", requestFilm.getDescription());
        parameters.put("release_date", requestFilm.getReleaseDate());
        parameters.put("duration", requestFilm.getDuration());

        long mpaId = requestFilm.getMpa() == null ? 1 : requestFilm.getMpa().getId();
        String sqlMpa = "select * from mpa where mpa_id = ?";
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(sqlMpa, mpaId);
        if (!mpaRows.next()) {
            throw new ParamsIncorrectException(String.format("Некорректный рейтинг id %d", mpaId));
        }
        parameters.put("mpa_id", mpaId);

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");

        Number id = simpleJdbcInsert.executeAndReturnKey(parameters);

        Long filmId = id.longValue();

        String sql = "merge into film_genres (film_id, genre_id) values (?, ?);";
        if (requestFilm.getGenres() != null) {
            for (Genre genre : requestFilm.getGenres()) {
                long genreId = genre.getId();
                String sqlGenre = "select * from genres where id = ?";
                SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sqlGenre, genreId);
                if (!genreRows.next()) {
                    throw new ParamsIncorrectException(String.format("Некорректный жанр id %d", genreId));
                }
                jdbcTemplate.update(sql, filmId, genreId);
            }
        }

        Film film = findFilmById(filmId);

        log.info("Новый пользователь: {}", film);

        return film;
    }

    @Override
    public Film updateFilm(Film requestFilm) {
        long filmId = requestFilm.getId();
        Film oldDataFilm = findFilmById(filmId);
        log.info("Найден фильм: {}", oldDataFilm);

        List<Genre> newGenres = requestFilm.getGenres();
        if (newGenres != null) {
            List<Genre> oldGenres = getGenresList(filmId);
            List<Genre> removedGenres = newGenres.stream()
                    .filter(genre -> !oldGenres.contains(genre))
                    .collect(Collectors.toList());

            String sqlDelete = "delete from film_genres where film_id = ? and genre_id = ?;";
            if (!removedGenres.isEmpty()) {
                for (Genre genre : removedGenres) {
                    jdbcTemplate.update(sqlDelete, filmId, genre.getId());
                }
            }

            String sqlMerge = "merge into film_genres (film_id, genre_id) values (?, ?);";
            for (Genre genre : newGenres) {
                long genreId = genre.getId();
                String sqlGenre = "select * from genres where id = ?";
                SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sqlGenre, genreId);
                if (!genreRows.next()) {
                    throw new ParamsIncorrectException(String.format("Некорректный жанр id %d", genreId));
                }
                jdbcTemplate.update(sqlMerge, filmId, genre.getId());
            }
        }

        long mpaId = requestFilm.getMpa().getId();
        String sql = "select * from mpa where mpa_id = ?";
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(sql, mpaId);
        if (!mpaRows.next()) {
            throw new ParamsIncorrectException(String.format("Некорректный рейтинг id %d", mpaId));
        }

        String sqlQuery = "update films set name = ?, " +
                "description = ?, release_date = ?, duration = ?, mpa_id = ? where id = ?";

        jdbcTemplate.update(sqlQuery,
                requestFilm.getName(),
                requestFilm.getDescription(),
                requestFilm.getReleaseDate(),
                requestFilm.getDuration(),
                mpaId,
                requestFilm.getId());

        Film film = findFilmById(filmId);

        log.info("Обновленный фильм: {}", film);

        return film;
    }

    @Override
    public Film addToLikes(long filmId, long userId) {
        Film film = findFilmById(filmId);
        log.info("Найден фильм: {}", film);

        String sqlQuery = "merge into likes(film_id, user_id) values (?, ?)";
        jdbcTemplate.update(sqlQuery,
                filmId,
                userId);

        return findFilmById(filmId);
    }

    @Override
    public Film deleteFromLikes(long filmId, long userId) {
        Film film = findFilmById(filmId);
        log.info("Найден фильм: {}", film);

        String sql = "delete from likes where film_id = ? and user_id = ?";
        jdbcTemplate.update(sql,
                filmId,
                userId);

        return findFilmById(filmId);
    }

    @Override
    public List<Film> findPopularFilms(int count) {
        String sql = "select f.id, f.name, f.description, f.release_date, f.duration, " +
                "count(l.film_id) as rate, f.mpa_id " +
                "from films as f " +
                "left join likes as l on f.id = l.film_id " +
                "group by f.id, f.name, f.description, f.release_date, f.duration, f.mpa_id " +
                "order by rate desc " +
                "limit ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), count);
    }

    private List<Genre> getGenresList(long id) {
        String sql = "select g.id " +
                "from genres as g " +
                "join film_genres as fg on g.id = fg.genre_id " +
                "where fg.film_id = ?;";

        return jdbcTemplate.query(sql, (rs, rowNum) -> genreDao.findGenreById(rs.getLong("id")), id);
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        long mpaId = rs.getLong("mpa_id");
        Mpa mpa = mpaDao.findMpaById(mpaId);
        List<Genre> genres = getGenresList(id);

        return Film.builder()
                .id(id)
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .rate(rs.getInt("rate"))
                .genres(genres)
                .mpa(mpa)
                .build();
    }
}
