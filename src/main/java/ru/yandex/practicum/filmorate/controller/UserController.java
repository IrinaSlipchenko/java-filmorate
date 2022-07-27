package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.UserValidator;

import javax.validation.Valid;
import java.util.List;

/**
 * The class helps listen to user request at "/users"
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    /**
     * @see UserService
     */
    private final UserService userService;

    /**
     * @see UserValidator
     */
    private final UserValidator userValidator;

    /**
     * @return all users saved in storage at the current moment
     * @see User
     */
    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
    }

    /**
     * @param id the specified identifier for the user to be searched
     * @return the user saved in storage under specified identifier
     * @see User
     */
    @GetMapping("/{id}")
    public User findUser(@PathVariable("id") Long id) {
        return userService.findUserById(id);
    }

    /**
     * @param user the json specified as user object with no identifier received to save in storage
     * @return user specified with identifier and saved with given parameters in storage
     * @see User
     * @see UserValidator
     */
    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("create user write log");
        return userService.create(userValidator.validateAndChange(user));
    }

    /**
     * @param user the json specified as user object with existing identifier received to save in storage
     * @return user specified with identifier and saved with updated parameters in storage
     * @see User
     * @see UserValidator
     */
    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("update user write log");
        return userService.update(userValidator.validateAndChange(user));
    }

    /**
     * @param id the specified identifier for the user to be deleted
     * @return user specified with identifier and deleted from storage
     * @see User
     */
    @DeleteMapping("/{id}")
    public User deleteUserById(@PathVariable Long id) {
        log.info("delete user id - " + id + " write it to the log");
        return userService.deleteUserById(id);
    }

    /**
     * @param id       the identifier for the user that adds other user to user's friends
     * @param friendId the identifier for the other user that added by user to user's friends
     * @return user specified with identifier and saved with updated parameters in storage
     * @see User
     */
    @PutMapping("/{id}/friends/{friendId}")
    public User friendAdd(@PathVariable Long id, @PathVariable Long friendId) {
        return userService.friendAdd(id, friendId);
    }

    /**
     * @param id       the identifier for the user that remove other user from user's friends
     * @param friendId the identifier for the other user that removed by user from user's friends
     * @return user specified with identifier and saved with updated parameters in storage
     * @see User
     */
    @DeleteMapping("/{id}/friends/{friendId}")
    public User friendDelete(@PathVariable Long id, @PathVariable Long friendId) {
        return userService.friendDelete(id, friendId);
    }

    /**
     * @param id the identifier for the user which friends are returned
     * @return all users saved at the current moment as friends of the user under specified identifier
     * @see User
     */
    @GetMapping("/{id}/friends")
    public List<User> allMyFriends(@PathVariable Long id) {
        return userService.allMyFriends(id);
    }

    /**
     * @param id      the identifier for user which friends would be compared to other user friends to find the common
     * @param otherId the identifier for other user which friends would be compared to user friends to find the common
     * @return all users saved at the current moment as common friends both for the user and other user
     * @see User
     */
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> commonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.commonFriends(id, otherId);
    }

    /**
     * @param id the identifier for the user which feeds would be returned
     * @return the feeds of the certain user under specified identifier
     * @see User
     * @see Feed
     */
    @GetMapping("/{id}/feed")
    public List<Feed> feed(@PathVariable Long id) {
        return userService.feed(id);
    }

}
