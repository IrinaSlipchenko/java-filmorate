package ru.yandex.practicum.filmorate.storage.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoSuchReactFind;
import ru.yandex.practicum.filmorate.model.Review;

/**
 * Class is provides database functionalities for review likes.
 */
@Component
@RequiredArgsConstructor
public class ReviewLikeDbStorageImpl {
    /**
     * @see JdbcTemplate
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * Return usefulness of review from storage by review id
     *
     * @param reviewId the specified as identifier of review
     * @return the usefulness of review
     * @see Review
     */
    public Integer getResultUseful(Long reviewId) {
        final String sql = "SELECT SUM( CASE WHEN  rl.is_useful IS NULL "
                + " THEN  0 ELSE CASE WHEN  rl.is_useful THEN  1 ELSE -1 END END) "
                + " AS uset FROM reviews AS r LEFT JOIN review_like AS rl ON r.review_id = rl.review_id "
                + " WHERE r.review_id = ? GROUP BY (r.review_id)";
        return jdbcTemplate.queryForObject(sql
                , (rs, rowNum) -> rs.getInt("uset")
                , reviewId);
    }

    /**
     * User add reaction on review
     *
     * @param idReview the specified as identifier of review to add a reaction on it
     * @param userId   the specified as identifier of user, which added a reaction
     * @param isUseful the specified as a type of reaction
     * @return does reaction add to review
     * @see Review
     */
    public Boolean addReaction(Long idReview, Long userId, Boolean isUseful) {
        final String sql = "MERGE INTO review_like ( review_id, user_id, is_useful ) VALUES ( ?, ? , ? )";
        return jdbcTemplate.update(sql, idReview, userId, isUseful) > 0;
    }

    /**
     * User delete reaction on review
     *
     * @param idReview the specified as identifier of review to delete a reaction on it
     * @param userId   the specified as identifier of user, which deleted a reaction
     * @param isUseful the specified as a type of reaction
     * @return does reaction delete from review
     * @throws NoSuchReactFind if reaction by review id, user id and type wasn't found in database
     * @see Review
     */
    public Boolean deleteReaction(Long idReview, Long userId, Boolean isUseful) {
        if (!containsReaction(idReview, userId, isUseful)) {
            if (isUseful) throw new NoSuchReactFind("Пользователь c ID = " + userId
                    + " не оставлял положительной реакции на отзыв с ID = " + idReview);
            throw new NoSuchReactFind("Пользователь c ID = " + userId
                    + " не оставлял отрицательной реакции на отзыв с ID = " + idReview);
        }
        final String sql = "DELETE FROM review_like WHERE review_id =? AND user_id = ? AND is_useful = ?";
        return jdbcTemplate.update(sql, idReview, userId, isUseful) > 0;
    }

    /**
     * Return does the database contain reaction by review id, user id and type
     *
     * @param idReview the specified as identifier of review to delete a reaction on it
     * @param userId   the specified as identifier of user, which deleted a reaction
     * @param isUseful the specified as a type of reaction
     * @return does database contain the reaction by review id, user id and type
     * @see Review
     */
    public Boolean containsReaction(Long idReview, Long userId, Boolean isUseful) {
        final String sql = "SELECT review_id FROM review_like WHERE review_id = ? AND user_id = ? AND is_useful = ?";
        return jdbcTemplate.queryForList(sql, idReview, userId, isUseful).size() > 0;
    }

}
