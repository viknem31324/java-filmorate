package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.UserValidation;

import java.util.List;

@Service
public class UserService {
    private final UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<User> findAllUsers() {
        return userDao.findAllUsers();
    }

    public User findUserById(long userId) {
        return userDao.findUserById(userId);
    }

    public User createUser(User user) {
        UserValidation.validation(user);

        return userDao.createUser(user);
    }

    public User updateUser(User user) {
        UserValidation.validation(user);

        return userDao.updateUser(user);
    }

    public List<User> findUserFriends(long id) {
        return userDao.findUserFriends(id);
    }

    public List<User> findMutualFriends(long id, long otherId) {
        return userDao.findMutualFriends(id, otherId);
    }

    public User addToFriends(long id, long friendId) {
        return userDao.addToFriends(id, friendId);
    }

    public User deleteFromFriends(long id, long friendId) {
        return userDao.deleteFromFriends(id, friendId);
    }
}
