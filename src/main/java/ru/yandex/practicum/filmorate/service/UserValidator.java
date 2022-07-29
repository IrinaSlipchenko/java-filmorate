package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashSet;

/**
 * The class helps verify and modify received by controllers user json object to specified requirements of User class
 */
@Component
public class UserValidator {

    /**
     * @param user object of User class needed to verify and if necessary change some parameters to required values,
     *             empties user friends set if it is determined as null by object itself,
     *             refreshing username as its login if it determined as null or blank string.
     * @return user object verified and modified according to requirements for friends and username
     * @see User
     */
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
