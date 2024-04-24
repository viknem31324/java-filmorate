package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.impl.MpaDaoImpl;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaDbDaoTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void testFindAllMpa() {
        MpaDaoImpl mpaDao = new MpaDaoImpl(jdbcTemplate);

        List<Mpa> mpaList = List.of(
                Mpa.builder().id(1).name("G").build(),
                Mpa.builder().id(2).name("PG").build(),
                Mpa.builder().id(3).name("PG-13").build(),
                Mpa.builder().id(4).name("R").build(),
                Mpa.builder().id(5).name("NC-17").build());

        List<Mpa> mpaRequestList = mpaDao.findAllMpa();

        assertEquals(mpaRequestList.size(), mpaList.size(), "Не верная длина списков");
        assertEquals(mpaRequestList.get(0), mpaList.get(0), "Разные рейтинги");
    }

    @Test
    public void testFindMpaById() {
        MpaDaoImpl mpaDao = new MpaDaoImpl(jdbcTemplate);

        Mpa mpa = Mpa.builder().id(1).name("G").build();
        Mpa mpaTest = mpaDao.findMpaById(mpa.getId());

        assertEquals(mpa, mpaTest, "Разные рейтинги");
    }
}
