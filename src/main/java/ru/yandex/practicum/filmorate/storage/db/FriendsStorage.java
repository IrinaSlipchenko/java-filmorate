package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

@Component
@RequiredArgsConstructor
public class FriendsStorage {

    private final JdbcTemplate jdbcTemplate;

    public void updateFriends(User user) {
        String sql = "DELETE FROM friends WHERE user_id=?";
        String sql2 = "INSERT INTO friends (user_id, friend_id) VALUES(?,?)";
        jdbcTemplate.update(sql, user.getId());
        user.getFriends().forEach(fId -> jdbcTemplate.update(sql2, user.getId(), fId));
    }

    public Boolean containsFriend(Long id, Long friendId){
        String sql = "SELECT user_id FROM friends WHERE user_id = ? AND friend_id = ?";
        return jdbcTemplate.queryForList(sql, id, friendId).size()>0;
    }
}
