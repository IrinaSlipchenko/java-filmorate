package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

/**
 * Class is provides database functionalities for friends.
 *
 * @see JdbcTemplate
 */
@Component
@RequiredArgsConstructor
public class FriendsStorageImpl {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Updates the user's friends
     *
     * @param user who needs to update friends
     * @see User
     */
    public void updateFriends(User user) {
        String sql = "DELETE FROM friends WHERE user_id=?";
        String sql2 = "INSERT INTO friends (user_id, friend_id) VALUES(?,?)";
        jdbcTemplate.update(sql, user.getId());
        user.getFriends().forEach(fId -> jdbcTemplate.update(sql2, user.getId(), fId));
    }

    /**
     * Checks if the user with identifier id has a friend with identifier friendId
     *
     * @param id       the identifier user
     * @param friendId the identifier friend
     * @return true or false
     */
    public Boolean containsFriend(Long id, Long friendId) {
        String sql = "SELECT user_id FROM friends WHERE user_id = ? AND friend_id = ?";
        return jdbcTemplate.queryForList(sql, id, friendId).size() > 0;
    }
}
