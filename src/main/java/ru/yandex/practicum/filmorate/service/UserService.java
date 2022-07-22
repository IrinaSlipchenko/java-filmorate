package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.db.FeedDbStorage;
import ru.yandex.practicum.filmorate.storage.db.FriendsStorage;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FeedDbStorage feedDbStorage;
    private final FriendsStorage friendsStorage;

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
        return userStorage.deleteUserById(userID);
    }

    public User friendAdd(Long id, Long friendId) {
        User user = userStorage.findUserById(id);
        userStorage.findUserById(friendId); // validate friend
        user.getFriends().add(friendId);
        if(!friendsStorage.containsFriend(id,friendId)){
            userStorage.update(user);
            feedDbStorage.add(Feed.builder()
                    .timestamp(new Timestamp(System.currentTimeMillis()).getTime())
                    .userId(id)
                    .eventType("FRIEND")
                    .operation("ADD")
                    .entityId(friendId)
                    .build());
        }
        userStorage.update(user);
        return user;
    }

    public User friendDelete(Long id, Long friendId) {
        User user = userStorage.findUserById(id);
        userStorage.findUserById(friendId); // validate friend
        user.getFriends().remove(friendId);
        userStorage.update(user);
        feedDbStorage.add(Feed.builder()
                .timestamp(new Timestamp(System.currentTimeMillis()).getTime())
                .userId(id)
                .eventType("FRIEND")
                .operation("REMOVE")
                .entityId(friendId)
                .build());
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

    public List<Feed> feed(Long id){
        userStorage.findUserById(id);
        return feedDbStorage.get(id);
    }
}
