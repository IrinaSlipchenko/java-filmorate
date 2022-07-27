package ru.yandex.practicum.filmorate.storage.db;


import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoSuchReactFind;

@Component
@RequiredArgsConstructor
public class ReviewLikeDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public Integer getResultUseful(Long reviewId) {
        final String sql = "SELECT SUM( CASE WHEN  rl.is_useful IS NULL THEN  0 ELSE CASE WHEN  rl.is_useful THEN  1 ELSE -1 END END) "
                + " AS uset FROM reviews AS r LEFT JOIN review_like AS rl ON r.review_id = rl.review_id "
                + " WHERE r.review_id = ? GROUP BY (r.review_id)";
        return jdbcTemplate.queryForObject(sql
                , (rs, rowNum) -> rs.getInt("uset")
                , reviewId);
    }

    public Boolean addReaction(Long idReview, Long userId, Boolean is_useful) {
        final String sql = "MERGE INTO review_like ( review_id, user_id, is_useful ) VALUES ( ?, ? , ? )";
        return jdbcTemplate.update(sql, idReview, userId, is_useful) > 0;
    }

    public Boolean deleteReaction(Long idReview, Long userId, Boolean is_useful) {
        if (!containsReaction(idReview, userId, is_useful)) {
            if (is_useful) throw new NoSuchReactFind("Пользователь c ID = " + userId
                    + " не оставлял положительной реакции на отзыв с ID = " + idReview);
            throw new NoSuchReactFind("Пользователь c ID = " + userId
                    + " не оставлял отрицательной реакции на отзыв с ID = " + idReview);
        }
        final String sql = "DELETE FROM review_like WHERE review_id =? AND user_id = ? AND is_useful = ?";
        return jdbcTemplate.update(sql, idReview, userId, is_useful) > 0;
    }

    public Boolean containsReaction(Long idReview, Long userId, Boolean is_useful) {
        final String sql = "SELECT review_id FROM review_like WHERE review_id = ? AND user_id = ? AND is_useful = ?";
        return jdbcTemplate.queryForList(sql, idReview, userId, is_useful).size() > 0;
    }

}
