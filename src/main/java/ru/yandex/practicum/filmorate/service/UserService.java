package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoSuchReviewIdException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public User findUserById(Long userID) {
        User user = userStorage.findUserById(userID);
        if (user == null) {
            throw new UserNotFoundException("Пользователь по ID = " + user.getId() + " не найден");
        }
        return userStorage.findUserById(userID);
    }

    public User deleteUserById(Long userID) {
        return userStorage.deleteUserById(userID);
    }

    public User friendAdd(Long id, Long friendId) {

        User user = userStorage.findUserById(id);
        userStorage.findUserById(friendId); // validate friend

        user.getFriends().add(friendId);
        userStorage.update(user);
        return user;
    }

    public User friendDelete(Long id, Long friendId) {
        User user = userStorage.findUserById(id);
        userStorage.findUserById(friendId); // validate friend

        user.getFriends().remove(friendId);
        userStorage.update(user);

        return user;
    }

    public List<User> allMyFriends(Long id) {
        User user = userStorage.findUserById(id);
        if (user == null) {
            throw new UserNotFoundException("Пользователь по ID = " + user.getId() + " не найден");
        }
        return userStorage.allMyFriends(id);
    }

    public List<User> commonFriends(Long id, Long otherId) {
        List<User> myFriends = allMyFriends(id);
        List<User> otherFriends = allMyFriends(otherId);

        return myFriends.stream().filter(otherFriends::contains).collect(Collectors.toList());
    }
}
