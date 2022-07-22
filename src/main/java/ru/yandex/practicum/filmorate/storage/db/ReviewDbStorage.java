package ru.yandex.practicum.filmorate.storage.db;


import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReviewDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private final ReviewLikeDbStorage reviewLikeDbStorage;
    private final  FeedDbStorage feedDbStorage;

    public Review add(Review review){
        final String sqlUserHaveReviewFilm = "SELECT * FROM reviews WHERE film_id = ? AND user_id = ?";
        if( jdbcTemplate.queryForList(sqlUserHaveReviewFilm,review.getFilmId(),review.getUserId()).size() > 0){
            throw new AlreadyExistException("Пользователь с ID = "
                    + review.getUserId() + " уже оставлял отзыв на фильм ID = " + review.getFilmId());
        }
        final String sql = "INSERT INTO reviews ( content, is_positive, user_id, film_id )" +
                " VALUES ( ? ,? ,? ,? )";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"review_id"});
            stmt.setString(1, review.getContent() );
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setLong(3,review.getUserId());
            stmt.setLong(4,review.getFilmId());
            return stmt;
        }, keyHolder);
        review.setReviewId(keyHolder.getKey().longValue());
        review.setUseful(reviewLikeDbStorage.getResultUseful(review.getReviewId()));
        feedDbStorage.add(Feed.builder()
                .timestamp(new Timestamp(System.currentTimeMillis()).getTime())
                .userId(review.getUserId())
                .eventType("REVIEW")
                .operation("ADD")
                .entityId(review.getReviewId())
                .build());
        return review;
    }

    public Review get(Long reviewId){
        final String sql = "SELECT * FROM reviews WHERE review_id = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToReview, reviewId);
    }

    public Review update(Review review){
        String sql = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
        jdbcTemplate.update(sql,review.getContent(),review.getIsPositive(),review.getReviewId());
        feedDbStorage.add(Feed.builder()
                .timestamp(new Timestamp(System.currentTimeMillis()).getTime())
                .userId(review.getUserId())
                .eventType("REVIEW")
                .operation("UPDATE")
                .entityId(review.getReviewId())
                .build());
        return get(review.getReviewId());
    }

    public Review delete( Long reviewId ) {
        Review review = get(reviewId);
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        jdbcTemplate.update(sql,review.getReviewId());
        feedDbStorage.add(Feed.builder()
                .timestamp(new Timestamp(System.currentTimeMillis()).getTime())
                .userId(review.getUserId())
                .eventType("REVIEW")
                .operation("REMOVE")
                .entityId(review.getReviewId())
                .build());
        return review;
    }

    public List<Long> getAllByFilmId (Long filmId, Integer count){
        final String sql = "SELECT r.review_id, "
                + " SUM( CASE WHEN  rl.is_useful IS NULL THEN  0 ELSE CASE WHEN  rl.is_useful THEN  1 ELSE -1 END END) "
                + " AS uset FROM reviews AS r LEFT JOIN review_like AS rl ON r.review_id = rl.review_id "
                + " WHERE r.film_id = ? GROUP BY (r.review_id) ORDER BY uset DESC LIMIT ?";
        return jdbcTemplate.query(sql,(rs, rowNum) -> rs.getLong("review_id"),filmId, count);

    }

    public List<Long> getAll (Integer count){
        final String sql = "SELECT r.review_id, "
        + " SUM( CASE WHEN  rl.is_useful IS NULL THEN  0 ELSE CASE WHEN  rl.is_useful THEN  1 ELSE -1 END END) "
        + " AS uset FROM reviews AS r LEFT JOIN review_like AS rl ON r.review_id = rl.review_id "
        + " GROUP BY (r.review_id) ORDER BY uset DESC LIMIT ?";
        return jdbcTemplate.query(sql,(rs, rowNum) -> rs.getLong("review_id"), count);
    }

    public Boolean containsIdReview(Long reviewId){
        final String sql = "SELECT review_id FROM reviews WHERE review_id = ?";
        return jdbcTemplate.queryForList(sql, reviewId).size()>0;
    }

    public Boolean addReaction(Long id, Long userId,Boolean isUseful){
        if(reviewLikeDbStorage.addReaction(id, userId, isUseful)) {
            return true;
        }
        return false;
    }

    public Boolean deleteLike (Long id, Long userId) {
        if( reviewLikeDbStorage.deleteReaction(id,userId)){
            return true;
        }
        return false;
    }
    public Boolean deleteDislike (Long id, Long userId) {
        if( reviewLikeDbStorage.deleteDislikeReaction(id,userId)){
            return true;
        }
        return false;
    }

    private Review mapRowToReview(ResultSet rs, int rowNum) throws SQLException {
        Review review =  Review.builder()
                .reviewId(rs.getLong("review_id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .userId(rs.getLong("user_id"))
                .filmId(rs.getLong("film_id"))
                .build();
        review.setUseful(reviewLikeDbStorage.getResultUseful(review.getReviewId()));
        return review;
    }

}
