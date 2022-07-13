package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage{
    private final Map<Long, User> users = new HashMap<>();

    private long id;

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public User create(User user) {
        user.setId(nextID());
        if ("".equals(user.getName()) || user.getName() == null) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }

    public User update(User user){
        if (users.containsKey(user.getId())) {
            if ("".equals(user.getName()) || user.getName() == null) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
            return user;
        }
        throw new UserNotFoundException("User not exist");
    }

    public User findUserById(Long userId) {
        return users.values().stream()
                .filter(u -> u.getId() == userId)
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException(String.format("%d User not exist", userId)));
    }

    private long nextID() {
        return ++id;
    }
}
