package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final static Logger log = LoggerFactory.getLogger(UserController.class);
    private Map<Long, User> users = new HashMap<>();

    private long id;

    @GetMapping
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        user.setId(nextID());
        log.info("create user write log");
        if ("".equals(user.getName()) || user.getName() == null) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("update user write log");
        if (users.containsKey(user.getId())) {
            if ("".equals(user.getName()) || user.getName() == null) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
            return user;
        }
        throw new ValidationException("User not exist");
    }

    private long nextID() {
        return ++id;
    }
}
