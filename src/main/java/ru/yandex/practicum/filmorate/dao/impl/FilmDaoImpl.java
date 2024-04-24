package ru.yandex.practicum.filmorate.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ParamsIncorrectException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
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

    @Autowired
    public FilmDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
            long mpaId = filmRows.getLong("mpa_id");
            Mpa mpa = Mpa.builder().id(mpaId).build();

            Film film = Film.builder()
                    .id(filmRows.getLong("id"))
                    .name(filmRows.getString("name"))
                    .description(filmRows.getString("description"))
                    .releaseDate(filmRows.getDate("release_date").toLocalDate())
                    .duration(filmRows.getInt("duration"))
                    .rate(filmRows.getInt("rate"))
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
            List<Genre> genres = requestFilm.getGenres();
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                    String sqlGenre = "select * from genres where id = ?";
                    SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sqlGenre, genres.get(i).getId());
                    if (!genreRows.next()) {
                        throw new ParamsIncorrectException(String.format("Некорректный жанр id %d", genres.get(i).getId()));
                    }

                    preparedStatement.setLong(1, filmId);
                    preparedStatement.setLong(2, genres.get(i).getId());
                }

                @Override
                public int getBatchSize() {
                    return genres.size();
                }
            });
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
            List<Genre> oldGenres = getGenresList(filmId).stream()
                    .map(item -> Genre.builder().id(item).build())
                    .collect(Collectors.toList());
            List<Genre> removedGenres = newGenres.stream()
                    .filter(genre -> !oldGenres.contains(genre))
                    .collect(Collectors.toList());

            String sqlDelete = "delete from film_genres where film_id = ? and genre_id = ?;";
            if (!removedGenres.isEmpty()) {
                jdbcTemplate.batchUpdate(sqlDelete, new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                        preparedStatement.setLong(1, filmId);
                        preparedStatement.setLong(2, removedGenres.get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return removedGenres.size();
                    }
                });
            }

            String sqlMerge = "merge into film_genres (film_id, genre_id) values (?, ?);";
            jdbcTemplate.batchUpdate(sqlMerge, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                    String sqlGenre = "select * from genres where id = ?";
                    SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sqlGenre, newGenres.get(i).getId());
                    if (!genreRows.next()) {
                        throw new ParamsIncorrectException(String.format("Некорректный жанр id %d", newGenres.get(i).getId()));
                    }

                    preparedStatement.setLong(1, filmId);
                    preparedStatement.setLong(2, newGenres.get(i).getId());
                }

                @Override
                public int getBatchSize() {
                    return newGenres.size();
                }
            });
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

    private List<Long> getGenresList(long id) {
        String sql = "select g.id " +
                "from genres as g " +
                "join film_genres as fg on g.id = fg.genre_id " +
                "where fg.film_id = ?;";

        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("id"), id);
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        long mpaId = rs.getLong("mpa_id");
        Mpa mpa = Mpa.builder().id(mpaId).build();

        return Film.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .rate(rs.getInt("rate"))
                .mpa(mpa)
                .build();
    }
}
