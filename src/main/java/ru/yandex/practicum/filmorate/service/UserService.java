package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        if (user.getFriends() != null) {
            user.getFriends().add(friendId);
        } else {
            Set<Long> friendsSet1 = new HashSet<>();
            friendsSet1.add(friendId);
            user.setFriends(friendsSet1);
        }
        if (myFriend.getFriends() != null) {
            myFriend.getFriends().add(id);
        }else {
            Set<Long> friendsSet2 = new HashSet<>();
            friendsSet2.add(id);
            myFriend.setFriends(friendsSet2);
        }
        return userStorage.findUserById(id);
    }

    public User friendDelete(Long id, Long friendId) {
        User user = userStorage.findUserById(id);
        User myFriend = userStorage.findUserById(friendId);
        if (user.getFriends() != null) {
            user.getFriends().remove(friendId);
        }
        if (myFriend.getFriends() != null) {
            myFriend.getFriends().remove(id);
        }
        return userStorage.findUserById(id);
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
