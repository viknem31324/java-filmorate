package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DataIncorrectException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Repository
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
        log.debug("id пользователя: {}", userId);

        return users.values().stream()
                .filter(user -> user.getId() == userId)
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с id %d не найден", userId)));
    }

    @Override
    public List<User> findUserFriends(long id) {
        log.debug("id пользователя: {}", id);

        Set<Long> friendsId = users.get(id).getFriends();
        log.debug("Список id друзей пользователя: {}", friendsId);

        return users.values().stream()
                .filter(user -> friendsId.contains(user.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findMutualFriends(long id, long otherId) {
        log.debug("id первого пользователя: {}", id);
        log.debug("id второго пользователя: {}", id);
        List<User> listUser = findUserFriends(id);
        log.debug("Список друзей первого пользователя: {}", listUser);

        Set<Long> listIdOtherUser = users.get(otherId).getFriends();
        log.debug("Список id друзей второго пользователя: {}", listIdOtherUser);

        return listUser.stream()
                .filter(user -> listIdOtherUser.contains(user.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public User createUser(User requestUser) {
        User user = requestUser.toBuilder()
                .id(userId++)
                .name(requestUser.getName() == null ? requestUser.getLogin() : requestUser.getName())
                .friends(requestUser.getFriends() == null ? new HashSet<>() : requestUser.getFriends())
                .build();

        log.debug("Текущий пользователь: {}", user);

        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User updateUser(User requestUser) {
        log.debug("Текущий пользователь: {}", requestUser);

        users.put(requestUser.getId(), requestUser);

        return requestUser;
    }

    @Override
    public User addToFriends(long id, long friendId) {
        log.debug("id первого пользователя: {}", id);
        log.debug("id второго пользователя: {}", friendId);

        Set<Long> userOneFriends = new HashSet<>(users.get(id).getFriends());
        userOneFriends.add(friendId);

        User userOne = users.get(id).toBuilder()
                .friends(userOneFriends)
                .build();

        users.put(userOne.getId(), userOne);

        Set<Long> userTwoFriends = new HashSet<>(users.get(friendId).getFriends());
        userTwoFriends.add(id);

        User userTwo = users.get(friendId).toBuilder()
                .friends(userTwoFriends)
                .build();

        users.put(userTwo.getId(), userTwo);

        return userOne;
    }

    @Override
    public User deleteFromFriends(long id, long friendId) {
        log.debug("id первого пользователя: {}", id);
        log.debug("id второго пользователя: {}", id);

        Set<Long> userOneFriends = new HashSet<>(users.get(id).getFriends());
        userOneFriends.remove(friendId);

        User userOne = users.get(id).toBuilder()
                .friends(userOneFriends)
                .build();

        users.put(userOne.getId(), userOne);

        Set<Long> userTwoFriends = new HashSet<>(users.get(friendId).getFriends());
        userTwoFriends.remove(id);

        User userTwo = users.get(friendId).toBuilder()
                .friends(userTwoFriends)
                .build();

        users.put(userTwo.getId(), userTwo);

        return userOne;
    }
}
