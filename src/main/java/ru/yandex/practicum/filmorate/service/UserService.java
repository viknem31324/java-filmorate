package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataIncorrectException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.validation.UserValidation;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final InMemoryUserStorage storage;

    @Autowired
    public UserService(InMemoryUserStorage storage) {
        this.storage = storage;
    }

    public List<User> findAllUsers() {
        return storage.findAllUsers();
    }

    public User findUserById(long userId) {
        return storage.findUserById(userId);
    }

    public List<User> findUserFriends(long id) {
        storage.findUserById(id);

        return storage.findUserFriends(id);
    }

    public List<User> findMutualFriends(long id, long otherId) {
        storage.findUserById(id);
        storage.findUserById(otherId);

        return storage.findMutualFriends(id, otherId);
    }

    public User createUser(User user) {
        UserValidation.validation(user);

        for (User userItem : storage.findAllUsers()) {
            if (userItem.getEmail().equals(user.getEmail())) {
                throw new UserAlreadyExistException("Пользователь с таким email уже существует!");
            }
        }

        return storage.createUser(user);
    }

    public User updateUser(User user) {
        UserValidation.validation(user);

        if (user == null) {
            throw new DataIncorrectException("Ошибка запроса");
        }

        storage.findUserById(user.getId());

        return storage.updateUser(user);
    }

    public User addToFriends(long id, long friendId) {
        storage.findUserById(id);
        storage.findUserById(friendId);
        return storage.addToFriends(id, friendId);
    }

    public User deleteFromFriends(long id, long friendId) {
        storage.findUserById(id);
        storage.findUserById(friendId);
        return storage.deleteFromFriends(id, friendId);
    }
}
