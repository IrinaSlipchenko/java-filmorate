package ru.yandex.practicum.filmorate.storage.db;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ReviewDbStorage {
    private final JdbcTemplate jdbcTemplate;
    private final ReviewLikeDbStorage reviewLikeDbStorage;

    @Autowired
    public ReviewDbStorage(JdbcTemplate jdbcTemplate, ReviewLikeDbStorage reviewLikeDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.reviewLikeDbStorage = reviewLikeDbStorage;
    }

    public Review add(Review review){
        final String sql = "INSERT INTO reviews ( content, is_positive, user_id, film_id, useful )" +
                " VALUES ( ? ,? ,? ,? ,? )";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"review_id"});
            stmt.setString(1, review.getContent() );
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setLong(3,review.getUserId());
            stmt.setLong(4,review.getFilmId());
            stmt.setLong(5, 0l);
            return stmt;
        }, keyHolder);
        review.setReviewId(keyHolder.getKey().longValue());
        review.setUseful(reviewLikeDbStorage.getResultUseful(review.getReviewId()));
        return review;
    }

    public Review get(Long reviewId){
        final String sql = "SELECT * FROM reviews WHERE review_id = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToReview, reviewId);
    }

    public Review update(Review review){
        String sql = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
        jdbcTemplate.update(sql,review.getContent(),review.getIsPositive(),review.getReviewId());
        return get(review.getReviewId());
    }

    public Review delete( Long reviewId ) {
        Review review = get(reviewId);
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        jdbcTemplate.update(sql,review.getReviewId());
        return review;
    }

    public List<Long> getAllByFilmId (Long filmId, Integer count){
        final String sql = "SELECT review_id FROM reviews  WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
        List<Long> result = new ArrayList<>();
        jdbcTemplate.query(sql,(rs, rowNum) -> result.add(rs.getLong("review_id")),filmId, count);
        return result;
    }

    public List<Long> getAll (Integer count){
        final String sql = "SELECT review_id FROM reviews ORDER BY useful DESC LIMIT ?";
        List<Long> result = new ArrayList<>();
        jdbcTemplate.query(sql,(rs, rowNum) -> result.add(rs.getLong("review_id")), count);
        return result;
    }

    public Boolean containsIdReview(Long reviewId){
        final String sql = "SELECT * FROM reviews WHERE review_id = ?";
        return jdbcTemplate.queryForList(sql, reviewId).size()>0;
    }

    public Boolean addReaction(Long id, Long userId,Boolean isUseful){
        if(reviewLikeDbStorage.addReaction(id, userId, isUseful)) {
            updateUseful(id);
            return true;
        }
        return false;
    }

    public Boolean deleteLike (Long id, Long userId) {
        if( reviewLikeDbStorage.deleteReaction(id,userId)){
            updateUseful(id);
            return true;
        }
        return false;
    }
    public Boolean deleteDislike (Long id, Long userId) {
        if( reviewLikeDbStorage.deleteDislikeReaction(id,userId)){
            updateUseful(id);
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

    private void updateUseful(Long idReview){
        final String sql = "UPDATE reviews SET useful = ? WHERE review_id = ?";
        jdbcTemplate.update(sql, reviewLikeDbStorage.getResultUseful(idReview),idReview);
    }





}
