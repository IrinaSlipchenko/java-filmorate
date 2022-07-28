package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> findAll();

    User create(User user);

    User update(User user);

    User findUserById(Long userId);

    User deleteUserById(Long userId);

    List<User> allMyFriends(Long id);

    Boolean containsIdUser(Long userId);
}
