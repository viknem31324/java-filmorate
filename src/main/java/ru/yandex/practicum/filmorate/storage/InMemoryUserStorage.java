package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationUserException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.UserValidation;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private long userId = 1;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findUserById(long userId) {
        log.info("id пользователя: {}", userId);

        return users.values().stream()
                .filter(user -> user.getId() == userId)
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id %d не найден", userId)));
    }

    @Override
    public List<User> findUserFriends(long id) {
        log.info("id пользователя: {}", id);

        if (!users.containsKey(id)) {
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден", id));
        }

        Set<Long> friendsId = users.get(id).getFriends();
        log.info("Список id друзей пользователя: {}", friendsId);
        return users.values().stream()
                .filter(user -> friendsId.contains(user.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findMutualFriends(long id, long otherId) {
        log.info("id первого пользователя: {}", id);
        log.info("id второго пользователя: {}", id);
        List<User> listUser = findUserFriends(id);
        log.info("Список друзей первого пользователя: {}", listUser);

        if (!users.containsKey(otherId)) {
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден", id));
        }

        Set<Long> listIdOtherUser = users.get(otherId).getFriends();
        log.info("Список id друзей второго пользователя: {}", listIdOtherUser);

        return listUser.stream()
                .filter(user -> listIdOtherUser.contains(user.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public User createUser(User requestUser) {
        UserValidation.validation(requestUser);
        User user = requestUser.toBuilder()
                .id(userId++)
                .name(requestUser.getName() == null ? requestUser.getLogin() : requestUser.getName())
                .build();

        log.info("Текущий пользователь: {}", user);

        for (User userItem : users.values()) {
            if (userItem.getEmail().equals(user.getEmail())) {
                throw new UserAlreadyExistException("Пользователь с таким email уже существует!");
            }
        }

        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User updateUser(User requestUser) {
        log.info("Текущий пользователь: {}", requestUser);

        if (!users.containsKey(requestUser.getId())) {
            throw new ValidationUserException("Пользователя не существует!");
        }

        UserValidation.validation(requestUser);

        users.put(requestUser.getId(), requestUser);

        return requestUser;
    }

    @Override
    public User addToFriends(long id, long friendId) {
        log.info("id первого пользователя: {}", id);
        log.info("id второго пользователя: {}", id);

        if (!users.containsKey(id)) {
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден", id));
        }

        if (!users.containsKey(friendId)) {
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден", friendId));
        }

        users.get(id).getFriends().add(friendId);
        return users.get(friendId);
    }

    @Override
    public User deleteFromFriends(long id, long friendId) {
        log.info("id первого пользователя: {}", id);
        log.info("id второго пользователя: {}", id);

        if (!users.containsKey(id)) {
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден", id));
        }

        if (!users.containsKey(friendId)) {
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден", friendId));
        }

        User user = users.get(id);
        user.getFriends().remove(friendId);
        return user;
    }
}
