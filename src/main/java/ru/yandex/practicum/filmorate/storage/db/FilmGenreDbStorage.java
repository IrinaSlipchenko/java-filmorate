package ru.yandex.practicum.filmorate.storage.db;


import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class FilmGenreDbStorage {

    private final JdbcTemplate jdbcTemplate;

    public void updateGenres(Film film) {
        if (film.getGenres() == null) {
            film.setGenres(new HashSet<>());
        }
        String sql = "DELETE FROM film_genre WHERE film_id=?";
        jdbcTemplate.update(sql, film.getId());
        String sql2 = "INSERT INTO film_genre (film_id, genre_id) VALUES(?,?)";
        film.getGenres().stream()
                .map(Genre::getId)
                .forEach(id -> jdbcTemplate.update(sql2, film.getId(), id));
    }
}
