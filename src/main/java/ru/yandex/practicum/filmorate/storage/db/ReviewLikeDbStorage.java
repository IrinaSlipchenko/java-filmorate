package ru.yandex.practicum.filmorate.storage.db;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoSuchReactFind;

@Component
public class ReviewLikeDbStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewLikeDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Integer getResultUseful(Long reviewId){
        final Integer[] result = {0};
        final String sql = "SELECT is_useful FROM review_like where review_id = ?";
        jdbcTemplate.query(sql
                , ((rs, rowNum) -> result[0] += (rs.getBoolean("is_useful")) ? 1 : -1 )
                , reviewId);
        return result[0];
    }

    public Boolean addReaction (Long idReview, Long userId, Boolean is_useful){
        final String sql = "MERGE INTO review_like ( review_id, user_id, is_useful ) VALUES ( ?, ? , ? )";
        return jdbcTemplate.update(sql,idReview,userId,is_useful) > 0;
    }

    public Boolean deleteReaction (Long idReview, Long userId) {
        if(!containsReaction(idReview, userId, true) || !(containsReaction(idReview, userId, false))){
            throw new NoSuchReactFind( "Пользователь c ID = " + userId
                    + "не оставлял реакции на отзыв с ID = " + idReview );
        }
        final String sql = "DELETE FROM review_like WHERE review_id =? AND user_id = ?";
        return jdbcTemplate.update(sql,idReview,userId) > 0;
    }

    public Boolean deleteDislikeReaction(Long idReview, Long userId) {
        if(!containsReaction(idReview, userId, false)) {
            throw new NoSuchReactFind( "Пользователь c ID = " + userId
                    + "не оставлял  отрицательной реакции на отзыв с ID = " + idReview );
        }
        final String sql = "DELETE FROM review_like WHERE review_id =? AND user_id = ? AND is_useful = ?";
        return jdbcTemplate.update(sql,idReview,userId,false) > 0;
    }

    public Boolean containsReaction (Long idReview, Long userId, Boolean is_useful) {
        final String sql = "SELECT * FROM review_like WHERE review_id = ? AND user_id = ?";
        return jdbcTemplate.update(sql,idReview,userId,is_useful) > 0;
    }

}
