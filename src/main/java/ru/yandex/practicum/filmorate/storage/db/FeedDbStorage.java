package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.feedEnum.EventType;
import ru.yandex.practicum.filmorate.model.feedEnum.OperationType;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import static ru.yandex.practicum.filmorate.model.feedEnum.EventType.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;


@Component
@RequiredArgsConstructor
public class FeedDbStorage implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;

    public Feed addLike(Long userId, OperationType operation, Long filmId) {
        return add(Feed.builder()
                .timestamp(new Timestamp(System.currentTimeMillis()).getTime())
                .userId(userId)
                .eventType(LIKE)
                .operation(operation)
                .entityId(filmId)
                .build());
    }

    public Feed addReview(Long userId, OperationType operation, Long reviewId) {
        return add(Feed.builder()
                .timestamp(new Timestamp(System.currentTimeMillis()).getTime())
                .userId(userId)
                .eventType(REVIEW)
                .operation(operation)
                .entityId(reviewId)
                .build());
    }

    public Feed addFriend(Long userId, OperationType operation, Long friendId) {
        return add(Feed.builder()
                .timestamp(new Timestamp(System.currentTimeMillis()).getTime())
                .userId(userId)
                .eventType(FRIEND)
                .operation(operation)
                .entityId(friendId)
                .build());
    }

    public Feed add(Feed feed) {
        final String sql = "INSERT INTO feed (event_time, user_id, event_type, operation, entity_id ) "
                + "VALUES ( ?, ?, ?, ?, ? )";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"event_id"});
            stmt.setTimestamp(1, new Timestamp(feed.getTimestamp()));
            stmt.setLong(2, feed.getUserId());
            stmt.setString(3, feed.getEventType().toString());
            stmt.setString(4, feed.getOperation().toString());
            stmt.setLong(5, feed.getEntityId());
            return stmt;
        }, keyHolder);
        return feed;
    }

    public List<Feed> get(Long userId) {
        final String sql = "SELECT * FROM feed WHERE user_id = ? ORDER BY event_time ASC";
        return jdbcTemplate.query(sql, this::mapRowToFeed, userId);
    }

    private Feed mapRowToFeed(ResultSet rs, int rowNum) throws SQLException {
        return Feed.builder()
                .timestamp(rs.getTimestamp("event_time").getTime())
                .userId(rs.getLong("user_id"))
                .eventType(EventType.valueOf(rs.getString("event_type")))
                .operation(OperationType.valueOf(rs.getString("operation")))
                .eventId(rs.getLong("event_id"))
                .entityId(rs.getLong("entity_id"))
                .build();
    }
}
