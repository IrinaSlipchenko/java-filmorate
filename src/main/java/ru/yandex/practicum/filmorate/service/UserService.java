package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Set;
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
        return userStorage.findUserById(userID);
    }

    public User deleteUserById(Long userID) {
        User user = userStorage.findUserById(userID);
        Set<Long> myFriends = user.getFriends();
        for (Long id : myFriends) {
            userStorage.findUserById(id).getFriends().remove(userID);
        }
        return userStorage.delete(userID);
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
        return userStorage.findUserById(id).getFriends().stream()
                .map(userStorage::findUserById)
                .collect(Collectors.toList());
    }

    public List<User> commonFriends(Long id, Long otherId) {
        List<User> myFriends = allMyFriends(id);
        List<User> otherFriends = allMyFriends(otherId);

        return myFriends.stream().filter(otherFriends::contains).collect(Collectors.toList());
    }
}
