package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;

@Component
public class UserValidator {
    public User validateAndChange(User user) {
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        if ("".equals(user.getName()) || user.getName() == null) {
            user.setName(user.getLogin());
        }
        return user;
    }
}
