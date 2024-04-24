package ru.yandex.practicum.filmorate.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class GenreDaoImpl implements GenreDao {
    private final Logger log = LoggerFactory.getLogger(GenreDaoImpl.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> findAllGenre() {
        String sql = "select * from genres";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
    }

    @Override
    public Genre findGenreById(long genreId) {
        String sql = "select * from genres where id = ?";
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sql, genreId);

        if (genreRows.next()) {
            Genre genre = Genre.builder()
                    .id(genreRows.getLong("id"))
                    .name(genreRows.getString("name"))
                    .build();

            log.info("Найден жанр: {} {}", genre.getId(), genre.getName());

            return genre;
        } else {
            log.info("Жанр с идентификатором {} не найден.", genreId);
            throw new GenreNotFoundException(String.format("Жанр с id %d не найден", genreId));
        }
    }

    @Override
    public List<Genre> findAllGenreByFilmId(long filmId) {
        String sql = "select g.id, g.name " +
                "from genres as g " +
                "join film_genres as fg on g.id = fg.genre_id " +
                "where fg.film_id = ?;";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), filmId);
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        return Genre.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .build();
    }
}
