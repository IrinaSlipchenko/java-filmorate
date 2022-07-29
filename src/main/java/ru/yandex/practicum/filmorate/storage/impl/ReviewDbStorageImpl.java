package ru.yandex.practicum.filmorate.storage.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


/**
 * Class is provides database functionalities for reviews.
 */
@Component
@RequiredArgsConstructor
public class ReviewDbStorageImpl implements ReviewStorage {
    /**
     * @see JdbcTemplate
     */
    private final JdbcTemplate jdbcTemplate;
    /**
     * @see ReviewLikeDbStorageImpl
     */
    private final ReviewLikeDbStorageImpl reviewLikeDbStorage;


    /**
     * Add review to storage
     *
     * @param review the specified as Review object without identifier to be saved in the storage
     * @return the review as Review object saved in storage with unique identifier and review useful
     * @throws AlreadyExistException if user have made review on the film
     * @see Review
     */
    public Review add(Review review) {
        final String sqlUserHaveReviewFilm = "SELECT * FROM reviews WHERE film_id = ? AND user_id = ?";
        if (jdbcTemplate.queryForList(sqlUserHaveReviewFilm, review.getFilmId(), review.getUserId()).size() > 0) {
            throw new AlreadyExistException("Пользователь с ID = "
                    + review.getUserId() + " уже оставлял отзыв на фильм ID = " + review.getFilmId());
        }
        final String sql = "INSERT INTO reviews ( content, is_positive, user_id, film_id )" +
                " VALUES ( ? ,? ,? ,? )";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"review_id"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setLong(3, review.getUserId());
            stmt.setLong(4, review.getFilmId());
            return stmt;
        }, keyHolder);
        review.setReviewId(keyHolder.getKey().longValue());
        review.setUseful(reviewLikeDbStorage.getResultUseful(review.getReviewId()));
        return review;
    }


    /**
     * Return review from storage by review id
     *
     * @param reviewId the specified as identifier of review
     * @return the review as Review object
     * @see Review
     */
    public Review get(Long reviewId) {
        final String sql = "SELECT * FROM reviews WHERE review_id = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToReview, reviewId);
    }

    /**
     * Update review in storage
     *
     * @param review the specified as Review object with identifier to be updated in the storage
     *               will be updated only review content and review type
     * @return the review as Review object
     * @see Review
     */
    public Review update(Review review) {
        String sql = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
        jdbcTemplate.update(sql, review.getContent(), review.getIsPositive(), review.getReviewId());
        return get(review.getReviewId());
    }

    /**
     * Delete review from the storage.
     *
     * @param reviewId the specified as identifier of review to remove from the storage
     * @return the review as Review object removed from storage with identifier equals reviewId
     * @see Review
     */
    public Review delete(Long reviewId) {
        Review review = get(reviewId);
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        jdbcTemplate.update(sql, review.getReviewId());
        return review;
    }

    /**
     * Returns as many reviews as specified in the query by count by film id, ordered by useful of review.
     *
     * @param filmId the specified as identifier of film
     * @param count  is the amount of reviews returned
     * @return a List containing the reviews id
     * @see Review
     */
    public List<Long> getAllByFilmId(Long filmId, Integer count) {
        final String sql = "SELECT r.review_id, "
                + " SUM( CASE WHEN  rl.is_useful IS NULL THEN  0 ELSE CASE WHEN  rl.is_useful THEN  1 ELSE -1 END END) "
                + " AS uset "
                + " FROM reviews AS r "
                + " LEFT JOIN review_like AS rl ON r.review_id = rl.review_id "
                + " WHERE r.film_id = ? "
                + " GROUP BY r.review_id "
                + " ORDER BY uset DESC "
                + "LIMIT ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("review_id"), filmId, count);

    }

    /**
     * Returns as many reviews as specified in the query by count, ordered by useful of review.
     *
     * @param count is the amount of reviews returned
     * @return a List containing the reviews id
     * @see Review
     */
    public List<Long> getAll(Integer count) {
        final String sql = "SELECT r.review_id, "
                + " SUM( CASE WHEN  rl.is_useful IS NULL THEN  0 ELSE CASE WHEN  rl.is_useful THEN  1 ELSE -1 END END) "
                + " AS uset "
                + " FROM reviews AS r "
                + "LEFT JOIN review_like AS rl ON r.review_id = rl.review_id "
                + " GROUP BY (r.review_id) "
                + " ORDER BY uset DESC "
                + " LIMIT ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("review_id"), count);
    }

    /**
     * Return does the database contain the review by id
     *
     * @param reviewId the specified as identifier of the review
     * @return does database contain the review by id
     * @see Review
     */
    public Boolean containsIdReview(Long reviewId) {
        final String sql = "SELECT review_id FROM reviews WHERE review_id = ?";
        return jdbcTemplate.queryForList(sql, reviewId).size() > 0;
    }

    /**
     * User add reaction on review
     *
     * @param id       the specified as identifier of review to add a reaction on it
     * @param userId   the specified as identifier of user, which added a reaction
     * @param isUseful the specified as a type of reaction
     * @return does reaction add to review
     * @see Review
     */
    public Boolean addReaction(Long id, Long userId, Boolean isUseful) {
        return reviewLikeDbStorage.addReaction(id, userId, isUseful);
    }

    /**
     * User delete reaction on review
     *
     * @param id       the specified as identifier of review to delete a reaction on it
     * @param userId   the specified as identifier of user, which deleted a reaction
     * @param isUseful the specified as a type of reaction
     * @return does reaction delete from review
     * @see Review
     */
    public Boolean deleteReaction(Long id, Long userId, Boolean isUseful) {
        return reviewLikeDbStorage.deleteReaction(id, userId, isUseful);
    }

    /**
     * Mapping a query result to Review object
     *
     * @param rs     the specified as identifier of ResultSet
     * @param rowNum the specified as number of record from ResultSet
     * @return review as Review object with useful
     * @see Review
     */
    private Review mapRowToReview(ResultSet rs, int rowNum) throws SQLException {
        Review review = Review.builder()
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
