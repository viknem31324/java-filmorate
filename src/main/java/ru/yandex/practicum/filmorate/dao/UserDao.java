package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserDao {
    User findUserById(long id);

    List<User> findAllUsers();

    User createUser(User requestUser);

    User updateUser(User requestUser);

    User addToFriends(long id, long friendId);

    User deleteFromFriends(long id, long friendId);

    List<User> findUserFriends(long id);

    List<User> findMutualFriends(long id, long otherId);
}