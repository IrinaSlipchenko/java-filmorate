package ru.yandex.practicum.filmorate.storage.memory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    private long id;

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public User create(User user) {
        user.setId(nextID());
        users.put(user.getId(), user);
        return user;
    }

    public User update(User user) {
        Long userId = user.getId();
        if (users.containsKey(userId)) {
            users.put(userId, user);
            return user;
        }
        throw new UserNotFoundException(userId + " id - User not exist");
    }

    public User findUserById(Long userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        }
        throw new UserNotFoundException(userId + " id - User not exist");
    }

    @Override
    public User deleteUserById(Long userId) {
        if (users.containsKey(userId)) {
            User user = users.get(userId);
            users.remove(userId);
            return user;
        }
        throw new UserNotFoundException(userId + " id - User not exist");
    }

    @Override
    public List<User> allMyFriends(Long id) {
        return null;
    }

    @Override
    public List<Film> recommendations(Long id) {
        return null;
    }

    private long nextID() {
        return ++id;
    }
}
