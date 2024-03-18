package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.List;

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
        return storage.findUserFriends(id);
    }

    public User createUser(User user) {
        return storage.createUser(user);
    }

    public User updateUser(User user) {
        return storage.updateUser(user);
    }

    public void addToFriends(long id, long friendId) {
        storage.addToFriends(id, friendId);
    }

    public void deleteFromFriends(long id, long friendId) {
        storage.deleteFromFriends(id, friendId);
    }
}
