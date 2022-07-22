package ru.yandex.practicum.filmorate.storage.db;


import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class LikesDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public void updateLikes(Film film) {
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        String sql = "DELETE FROM FILM_LIKES WHERE film_id=?";
        jdbcTemplate.update(sql, film.getId());
        String sql2 = "INSERT INTO FILM_LIKES (film_id, user_id) VALUES(?,?)";
        film.getLikes().forEach(id -> jdbcTemplate.update(sql2, film.getId(), id));
    }

    public Set<Long> getLikesByFilmId(Long id) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id=?";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, i) -> rs.getLong("user_id"), id));
    }

    public Boolean containsLike (Long id, Long userId){
        String sql = "SELECT user_id FROM film_likes WHERE film_id = ? AND user_id = ?";
        return jdbcTemplate.queryForList(sql, id, userId).size()>0;
    }
}
