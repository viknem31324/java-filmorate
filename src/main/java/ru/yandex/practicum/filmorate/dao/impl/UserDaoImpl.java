package ru.yandex.practicum.filmorate.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class UserDaoImpl implements UserDao {
    private final Logger log = LoggerFactory.getLogger(UserDaoImpl.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findAllUsers() {
        return jdbcTemplate.query("select * from users",
                (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User findUserById(long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where id = ?", id);
        Set<Long> friendsIds = new HashSet<>(getFriendsIdsList(id));

        if (userRows.next()) {
            User user = User.builder()
                    .id(userRows.getLong("id"))
                    .email(userRows.getString("email"))
                    .login(userRows.getString("login"))
                    .name(userRows.getString("name"))
                    .birthday(userRows.getDate("birthday").toLocalDate())
                    .friends(friendsIds)
                    .build();

            log.info("Найден пользователь: {} {}", user.getId(), user.getName());

            return user;
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            throw  new UserNotFoundException(String.format("Пользователь с id %d не найден", id));
        }
    }


    @Override
    public User createUser(User requestUser)  {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where email = ?",
                requestUser.getEmail());

        if (userRows.next()) {
            throw new UserAlreadyExistException("Пользователь с таким email уже существует!");
        }

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("email", requestUser.getEmail());
        parameters.put("login", requestUser.getLogin());
        parameters.put("name", requestUser.getName() == null ? requestUser.getLogin() : requestUser.getName());
        parameters.put("birthday", requestUser.getBirthday());

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        Number id = simpleJdbcInsert.executeAndReturnKey(parameters);

        User user = findUserById(id.longValue());

        log.info("Новый пользователь: {}", user);

        return user;
    }

    @Override
    public User updateUser(User requestUser) {
        long id = requestUser.getId();
        User oldDataUser = findUserById(id);
        log.info("Найден пользователь: {}", oldDataUser);

        String sqlQuery = "update users set email = ?, login = ?, name = ?, birthday = ? where id = ?";

        jdbcTemplate.update(sqlQuery,
                requestUser.getEmail(),
                requestUser.getLogin(),
                requestUser.getName() == null ? requestUser.getLogin() : requestUser.getName(),
                requestUser.getBirthday(),
                requestUser.getId());

        User user = findUserById(id);

        log.info("Обновленный пользователь: {}", user);

        return user;
    }

    @Override
    public User addToFriends(long id, long friendId) {
        User oneUser = findUserById(id);
        log.info("Найден пользователь: {}", oneUser);

        User secondUser = findUserById(friendId);
        log.info("Найден пользователь: {}", secondUser);

        String sqlQuery = "merge into friends(user_id, friend_id, is_confirm) values (?, ?, ?);";
        boolean check = isSendRequestForFriend(friendId);

        jdbcTemplate.update(sqlQuery,
                id,
                friendId,
                check);

        return findUserById(id);
    }

    @Override
    public User deleteFromFriends(long id, long friendId) {
        User oneUser = findUserById(id);
        log.info("Найден пользователь: {}", oneUser);

        User secondUser = findUserById(friendId);
        log.info("Найден пользователь: {}", secondUser);

        String sqlDelete = "delete from friends where user_id = ? and friend_id = ?";
        jdbcTemplate.update(sqlDelete,
                id,
                friendId);

        return findUserById(id);
    }

    @Override
    public List<User> findUserFriends(long id) {
        User user = findUserById(id);
        log.info("Найден пользователь: {}", user);

        String sql = "select distinct u.id, u.email, u.login, u.name, u.birthday " +
                "from users u " +
                "join friends f on u.id = f.friend_id " +
                "where f.user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id);
    }

    @Override
    public List<User> findMutualFriends(long id, long otherId) {
        User oneUser = findUserById(id);
        log.info("Найден пользователь: {}", oneUser);

        User secondUser = findUserById(otherId);
        log.info("Найден пользователь: {}", secondUser);

        String sql = "select u.id, u.email, u.login, u.name, u.birthday " +
                "from friends as f1 " +
                "join friends as f2 on f1.friend_id = f2.friend_id " +
                "join users as u on f1.friend_id = u.id " +
                "where f1.user_id = ? and f2.user_id = ?;";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id, otherId);
    }

    private boolean isSendRequestForFriend(long userId) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet("select * from friends where user_id = ?;", userId);

        return rs.next();
    }

    private List<Long> getFriendsIdsList(long id) {
        String sql = "select u2.id " +
                "from friends as f " +
                "join users as u1 on f.user_id = u1.id " +
                "join users as u2 on f.friend_id = u2.id " +
                "where u1.id = ?";

        return jdbcTemplate.queryForList(sql, Long.class, id);
    }

    private User makeUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        Set<Long> friendsIds = new HashSet<>(getFriendsIdsList(id));

        return User.builder()
                .id(id)
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .friends(friendsIds)
                .build();
    }
}
