package ru.yandex.practicum.filmorate.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class MpaDaoImpl implements MpaDao {
    private final Logger log = LoggerFactory.getLogger(MpaDaoImpl.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDaoImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Override
    public List<Mpa> findAllMpa() {
        String sql = "select * from mpa";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
    }

    @Override
    public Mpa findMpaById(long mpaId) {
        String sql = "select * from mpa where mpa_id = ?";
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(sql, mpaId);

        if (mpaRows.next()) {
            Mpa mpa = Mpa.builder()
                    .id(mpaRows.getLong("mpa_id"))
                    .name(mpaRows.getString("name"))
                    .build();

            log.info("Найден рейтинг: {} {}", mpa.getId(), mpa.getName());

            return mpa;
        } else {
            log.info("Рейтинг с идентификатором {} не найден.", mpaId);
            throw new MpaNotFoundException(String.format("Рейтинг с id %d не найден", mpaId));
        }
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        return Mpa.builder()
                .id(rs.getLong("mpa_id"))
                .name(rs.getString("name"))
                .build();
    }
}
