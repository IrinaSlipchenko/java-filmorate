package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.feedEnum.OperationType;
import static ru.yandex.practicum.filmorate.model.feedEnum.EventType.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;



@Component
@RequiredArgsConstructor
public class FeedDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public Feed addLike(Long user_id, OperationType operation, Long film_id ){
        return add (Feed.builder()
                .timestamp(new Timestamp(System.currentTimeMillis()).getTime())
                .userId(user_id)
                .eventType(LIKE.toString())
                .operation(operation.toString())
                .entityId(film_id)
                .build() );
    }
    public Feed addReview(Long user_id, OperationType operation, Long review_id ){
        return add ( Feed.builder()
                .timestamp(new Timestamp(System.currentTimeMillis()).getTime())
                .userId(user_id)
                .eventType(REVIEW.toString())
                .operation(operation.toString())
                .entityId(review_id)
                .build() );
    }

    public Feed addFriend(Long user_id, OperationType operation, Long friend_id ){
        return add (Feed.builder()
                .timestamp(new Timestamp(System.currentTimeMillis()).getTime())
                .userId(user_id)
                .eventType(FRIEND.toString())
                .operation(operation.toString())
                .entityId(friend_id)
                .build() );
    }

    public Feed add (Feed feed){
        final String sql = "INSERT INTO feed (event_time, user_id, event_type, operation, entity_id ) "
                + "VALUES ( ?, ?, ?, ?, ? )";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"event_id"});
            stmt.setTimestamp(1, new Timestamp(feed.getTimestamp()) );
            stmt.setLong(2,feed.getUserId());
            stmt.setString(3,feed.getEventType());
            stmt.setString(4,feed.getOperation());
            stmt.setLong(5,feed.getEntityId());
            return stmt;
        },keyHolder);
        return feed;
    }

    public List<Feed> get (Long user_id){
        final String sql = "SELECT * FROM feed WHERE user_id = ? ORDER BY event_time ASC";
        return jdbcTemplate.query(sql,this::mapRowToFeed, user_id);
    }

    private Feed mapRowToFeed(ResultSet rs, int rowNum) throws SQLException {
        return Feed.builder()
                .timestamp(rs.getTimestamp("event_time").getTime())
                .userId(rs.getLong("user_id"))
                .eventType(rs.getString("event_type"))
                .operation(rs.getString("operation"))
                .eventId(rs.getLong("event_id"))
                .entityId(rs.getLong("entity_id"))
                .build();
    }
}
