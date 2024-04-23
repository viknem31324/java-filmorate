package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.dao.impl.UserDaoImpl;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbDaoTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void testFindAllUser() {
        UserDaoImpl userDao = new UserDaoImpl(jdbcTemplate);

        User newUser = User.builder()
                .email("user@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(new HashSet<>())
                .build();
        List<User> newUsers = List.of(newUser);
        User createdUser = userDao.createUser(newUser);
        User myUser = newUser.toBuilder().id(createdUser.getId()).build();

        List<User> users = userDao.findAllUsers();
        assertEquals(newUsers.size(), users.size(), "Некорректная длина списка");
        assertEquals(myUser, users.get(0), "Пользователи не равны");
    }

    @Test
    public void testFindUserById() {
        UserDaoImpl userDao = new UserDaoImpl(jdbcTemplate);

        User newUser = User.builder()
                .email("user@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(new HashSet<>())
                .build();

        User createdUser = userDao.createUser(newUser);
        User myUser = newUser.toBuilder().id(createdUser.getId()).build();
        User savedUser = userDao.findUserById(createdUser.getId());

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(myUser);
    }

    @Test
    public void testCreateAndUpdateUser() {
        UserDaoImpl userDao = new UserDaoImpl(jdbcTemplate);

        User newUser = User.builder()
                .email("user@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(new HashSet<>())
                .build();

        User createdUser = userDao.createUser(newUser);
        User myUser = newUser.toBuilder().id(createdUser.getId()).build();
        User savedUser = userDao.findUserById(createdUser.getId());

        assertThat(savedUser)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(myUser);

        User updatedUser = User.builder()
                .id(savedUser.getId())
                .email("user@email.ru")
                .login("vanya123")
                .name("Kolya Ivanov")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(new HashSet<>())
                .build();

        User updatedUserDb = userDao.updateUser(updatedUser);

        assertThat(updatedUserDb)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(updatedUser);
    }

    @Test
    public void testAddAndDeleteToFriends() {
        UserDaoImpl userDao = new UserDaoImpl(jdbcTemplate);
        User userOne = User.builder()
                .email("user1@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(new HashSet<>())
                .build();
        User createdUserOne = userDao.createUser(userOne);
        User myUserOne = userOne.toBuilder().id(createdUserOne.getId()).build();

        User userTwo = User.builder()
                .email("user2@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(new HashSet<>())
                .build();
        User createdUserTwo = userDao.createUser(userTwo);
        User myUserTwo = userTwo.toBuilder().id(createdUserTwo.getId()).build();

        User testUser1 = userDao.addToFriends(myUserOne.getId(), myUserTwo.getId());
        List<Long> friends1 = new ArrayList<>(testUser1.getFriends());

        assertEquals(1, friends1.size(), "Некорректная длина списка");
        assertEquals(myUserTwo.getId(), friends1.get(0), "Друзья не совпадают");

        User testUser2 = userDao.deleteFromFriends(myUserOne.getId(), myUserTwo.getId());
        List<Long> friends2 = new ArrayList<>(testUser2.getFriends());

        assertEquals(0, friends2.size(), "Некорректная длина списка");
    }

    @Test
    public void testFindUserFriends() {
        UserDaoImpl userDao = new UserDaoImpl(jdbcTemplate);
        User userOne = User.builder()
                .email("user1@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(new HashSet<>())
                .build();
        User createdUserOne = userDao.createUser(userOne);
        User myUserOne = userOne.toBuilder().id(createdUserOne.getId()).build();

        User userTwo = User.builder()
                .email("user2@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(new HashSet<>())
                .build();
        User createdUserTwo = userDao.createUser(userTwo);
        User myUserTwo = userTwo.toBuilder().id(createdUserTwo.getId()).build();

        userDao.addToFriends(myUserOne.getId(), myUserTwo.getId());

        List<User> friendslist = userDao.findUserFriends(myUserOne.getId());

        assertEquals(myUserTwo, friendslist.get(0), "Друзья не совпадают");
    }

    @Test
    public void testFindMutualFriends() {
        UserDaoImpl userDao = new UserDaoImpl(jdbcTemplate);
        User userOne = User.builder()
                .email("user1@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(new HashSet<>())
                .build();
        User createdUserOne = userDao.createUser(userOne);
        User myUserOne = userOne.toBuilder().id(createdUserOne.getId()).build();

        User userTwo = User.builder()
                .email("user2@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(new HashSet<>())
                .build();
        User createdUserTwo = userDao.createUser(userTwo);
        User myUserTwo = userTwo.toBuilder().id(createdUserTwo.getId()).build();

        User userThree = User.builder()
                .email("user3@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .friends(new HashSet<>())
                .build();
        User createdUserThree = userDao.createUser(userThree);
        User myUserThree = userThree.toBuilder().id(createdUserThree.getId()).build();

        userDao.addToFriends(myUserOne.getId(), myUserThree.getId());
        userDao.addToFriends(myUserTwo.getId(), myUserThree.getId());

        List<User> mutialFriendslist = userDao.findMutualFriends(myUserOne.getId(), myUserTwo.getId());

        assertEquals(myUserThree, mutialFriendslist.get(0), "Друзья не совпадают");
    }
}