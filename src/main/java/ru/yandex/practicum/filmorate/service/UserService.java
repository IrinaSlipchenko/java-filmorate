package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.db.FeedDbStorage;
import ru.yandex.practicum.filmorate.storage.db.FriendsStorage;

import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.model.feedEnum.OperationType.ADD;
import static ru.yandex.practicum.filmorate.model.feedEnum.OperationType.REMOVE;

/**
 * Class is provide business functionalities for users.
 */
@Service
@RequiredArgsConstructor
public class UserService {
    /**
     * @see UserStorage
     */
    private final UserStorage userStorage;
    /**
     * @see FeedDbStorage
     */
    private final FeedDbStorage feedDbStorage;
    /**
     * @see FriendsStorage
     */
    private final FriendsStorage friendsStorage;


    /**
     * Find and returns all users in the storage
     *
     * @return a List or users as Users objects which contains in storage
     * @see User
     * @see UserStorage
     */
    public List<User> findAll() {
        return userStorage.findAll();
    }

    /**
     * Add user to storage
     *
     * @param user the specified as User object without identifier to be saved in the storage
     * @return the user as User object saved in storage with unique identifier
     * @see User
     * @see UserStorage
     */
    public User create(User user) {
        return userStorage.create(user);
    }

    /**
     * Update user data in the storage
     *
     * @param user the specified as User object with identifier to be updated in the storage
     * @return the user as User object updated in the storage
     * @see User
     * @see UserStorage
     */
    public User update(User user) {
        return userStorage.update(user);
    }

    /**
     * Find and return user data from the storage
     *
     * @param userID the specified as identifier of user to get from the storage
     * @return the user as User object saved in storage with identifier equals userID
     * @see User
     * @see UserStorage
     */
    public User findUserById(Long userID) {
        return userStorage.findUserById(userID);
    }

    /**
     * Delete user from the storage
     *
     * @param userID the specified as identifier of user to remove from the storage
     * @return the user as User object removed from storage with identifier equals userId
     * @see User
     * @see UserStorage
     */
    public User deleteUserById(Long userID) {
        return userStorage.deleteUserById(userID);
    }

    /**
     * User add friend. To feedStorage added an entity about event.
     *
     * @param id       the specified as identifier of user, which want to add a friend
     * @param friendId the specified as identifier of user, which added to the friends of user
     * @return the user as User object from storage with identifier equals id
     * @see User
     * @see UserStorage
     * @see FeedDbStorage
     */
    public User friendAdd(Long id, Long friendId) {
        User user = userStorage.findUserById(id);
        userStorage.findUserById(friendId); // validate friend
        user.getFriends().add(friendId);
        if (!friendsStorage.containsFriend(id, friendId)) {
            userStorage.update(user);
            feedDbStorage.addFriend(id, ADD, friendId);
        } else userStorage.update(user);
        return user;
    }

    /**
     * User delete friend. To feedStorage added an entity about event.
     *
     * @param id       the specified as identifier of user, which want to delete a friend
     * @param friendId the specified as identifier of user, which deleted from the friends of user
     * @return the user as User object from storage with identifier equals id
     * @see User,
     * @see UserStorage
     * @see FeedDbStorage
     */
    public User friendDelete(Long id, Long friendId) {
        User user = userStorage.findUserById(id);
        userStorage.findUserById(friendId); // validate friend
        user.getFriends().remove(friendId);
        userStorage.update(user);
        feedDbStorage.addFriend(id, REMOVE, friendId);
        return user;
    }

    /**
     * Returns all friends of user.
     *
     * @param id the specified as identifier of user
     * @return a List containing the friends as User Objects
     * @throws UserNotFoundException if the user not found in the storage by ID
     * @see User
     * @see UserStorage
     */
    public List<User> allMyFriends(Long id) {
        User user = userStorage.findUserById(id);
        if (user == null) {
            throw new UserNotFoundException("Пользователь по ID = " + user.getId() + " не найден");
        }
        return userStorage.allMyFriends(id);
    }

    /**
     * Returns a common friends of two users.
     *
     * @param id      the specified as identifier of first user
     * @param otherId the specified as identifier of second user
     * @return a List containing the common friends as User Objects
     * @see User
     */
    public List<User> commonFriends(Long id, Long otherId) {
        List<User> myFriends = allMyFriends(id);
        List<User> otherFriends = allMyFriends(otherId);

        return myFriends.stream().filter(otherFriends::contains).collect(Collectors.toList());
    }

    /**
     * Returns the events by user
     *
     * @param id the specified as identifier of user
     * @return a List containing the events by user as Feed Objects
     * @throws UserNotFoundException if the user not found in the storage by ID
     * @see Feed
     */
    public List<Feed> feed(Long id) {
        if (!userStorage.containsIdUser(id)) {
            throw new UserNotFoundException(id + " id - user not found");
        }
        return feedDbStorage.get(id);
    }

    public List<Film> recommendations (Long id) {
        return userStorage.recommendations(id);
    }
}
