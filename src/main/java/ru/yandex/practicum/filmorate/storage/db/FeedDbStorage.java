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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import static ru.yandex.practicum.filmorate.model.feedEnum.EventType.*;


/**
 * Class is provides database functionalities for feed of events.
 */
@Component
@RequiredArgsConstructor
public class FeedDbStorage implements FeedStorage {
    /**
     * @see JdbcTemplate
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * Create and add feed of event add/delete like film by user to storage
     *
     * @param userId    the specified as user identifier, which were added or deleted a like to the film
     * @param operation the specified operation (add or delete) which was made by user
     * @param filmId    the specified as film identifier, which were liked by user
     * @return the feed as Feed object saved in storage with unique identifier
     * @see Feed
     */
    public Feed addLike(Long userId, OperationType operation, Long filmId) {
        return add(Feed.builder()
                .timestamp(new Timestamp(System.currentTimeMillis()).getTime())
                .userId(userId)
                .eventType(LIKE)
                .operation(operation)
                .entityId(filmId)
                .build());
    }

    /**
     * Create and add feed of event add/update/delete Review by user to storage
     *
     * @param userId    the specified as user identifier, which were added (deleted or updated) a review
     * @param operation the specified operation (add, update or delete) which was made by user
     * @param reviewId  the specified as review identifier, which were added (deleted or updated) by user
     * @return the feed as Feed object saved in storage with unique identifier
     * @see Feed
     */
    public Feed addReview(Long userId, OperationType operation, Long reviewId) {
        return add(Feed.builder()
                .timestamp(new Timestamp(System.currentTimeMillis()).getTime())
                .userId(userId)
                .eventType(REVIEW)
                .operation(operation)
                .entityId(reviewId)
                .build());
    }

    /**
     * Create and add feed of event add/delete friend by user to storage
     *
     * @param userId    the specified as user identifier, which were added or deleted a friend
     * @param operation the specified operation (add or delete) which was made by user
     * @param friendId  the specified as user identifier
     *                  , which were added like a friend or delete from the friends by user
     * @return the feed as Feed object saved in storage with unique identifier
     * @see Feed
     */
    public Feed addFriend(Long userId, OperationType operation, Long friendId) {
        return add(Feed.builder()
                .timestamp(new Timestamp(System.currentTimeMillis()).getTime())
                .userId(userId)
                .eventType(FRIEND)
                .operation(operation)
                .entityId(friendId)
                .build());
    }

    /**
     * Add feed of event to storage
     *
     * @param feed the specified as Feed object without identifier to be saved in the storage
     * @return the feed as Feed object saved in storage with unique identifier
     * @see Feed
     */
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

    /**
     * Returns the events by user
     *
     * @param userId the specified as identifier of user
     * @return a List containing the events by user as Feed Objects
     * @see Feed
     */
    public List<Feed> get(Long userId) {
        final String sql = "SELECT * FROM feed WHERE user_id = ? ORDER BY event_time ASC";
        return jdbcTemplate.query(sql, this::mapRowToFeed, userId);
    }

    /**
     * Mapping a query result to Feed object
     *
     * @param rs     the specified as identifier of ResultSet
     * @param rowNum the specified as number of record from ResultSet
     * @return the feed as Feed object with unique identifier
     * @see Feed
     */
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
