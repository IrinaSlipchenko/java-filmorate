package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
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
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return user;
        }
        throw new UserNotFoundException("User not exist");
    }

    public User findUserById(Long userId) {
        return users.values().stream()
                .filter(u -> Objects.equals(u.getId(), userId))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException(String.format("%d User not exist", userId)));
    }

    private long nextID() {
        return ++id;
    }
}
