package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

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
        return userStorage.findUserById(userID);
    }

    public User friendAdd(Long id, Long friendId) {
        User user = userStorage.findUserById(id);
        User myFriend = userStorage.findUserById(friendId);

        user.getFriends().add(friendId);
        myFriend.getFriends().add(id);

        return user;
    }

    public User friendDelete(Long id, Long friendId) {
        User user = userStorage.findUserById(id);
        User myFriend = userStorage.findUserById(friendId);

        user.getFriends().remove(friendId);
        myFriend.getFriends().remove(id);

        return user;
    }

    public List<User> allMyFriends(Long id) {
        return userStorage.findAll().stream()
                .filter(user -> user.getFriends().contains(id))
                .collect(Collectors.toList());
    }

    public List<User> commonFriends(Long id, Long otherId) {
        List<User> myFriends = userStorage.findAll().stream()
                .filter(user -> user.getFriends().contains(id)).collect(Collectors.toList());

        List<User> otherFriends = userStorage.findAll().stream()
                .filter(user -> user.getFriends().contains(otherId)).collect(Collectors.toList());

        return myFriends.stream().filter(otherFriends::contains).collect(Collectors.toList());
    }
}
