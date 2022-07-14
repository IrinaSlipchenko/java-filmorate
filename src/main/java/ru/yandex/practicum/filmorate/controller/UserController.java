package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.UserValidator;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;
    private final UserValidator userValidator;

    @Autowired
    public UserController(UserService userService, UserValidator userValidator) {
        this.userService = userService;
        this.userValidator = userValidator;
    }

    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public User findUser(@PathVariable("userId") Long userId) {
        return userService.findUserById(userId);
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("create user write log");
        return userService.create(userValidator.validateAndChange(user));
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("update user write log");
        return userService.update(userValidator.validateAndChange(user));
    }

    @DeleteMapping("/{id}")
    public User deleteUserById(@PathVariable Long id) {
        log.info("delete user id - " + id + " write it to the log");
        return userService.deleteUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User friendAdd(@PathVariable Long id, @PathVariable Long friendId) {
        return userService.friendAdd(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User friendDelete(@PathVariable Long id, @PathVariable Long friendId) {
        return userService.friendDelete(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> allMyFriends(@PathVariable Long id) {
        return userService.allMyFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> commonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.commonFriends(id, otherId);
    }
}
