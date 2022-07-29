package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Class is provides database functionalities for films and genres.
 *
 * @see GenreDbStorageImpl
 * @see JdbcTemplate
 */
@Component
@RequiredArgsConstructor
public class FilmGenreDbStorageImpl {

    private final GenreDbStorageImpl genreDbStorage;
    private final JdbcTemplate jdbcTemplate;

    /**
     * Updates the genres of the film
     *
     * @param film the movie for which we want to update the genres
     * @see Film
     */
    public void updateGenres(Film film) {
        if (film.getGenres() == null) {
            film.setGenres(new TreeSet<>());
        }
        String sql = "DELETE FROM film_genre WHERE film_id=?";
        jdbcTemplate.update(sql, film.getId());
        String sql2 = "INSERT INTO film_genre (film_id, genre_id) VALUES(?,?)";
        film.getGenres().stream()
                .map(Genre::getId).sorted()
                .forEach(id -> jdbcTemplate.update(sql2, film.getId(), id));
    }

    /**
     * Will return all genres that belong to the given movie
     *
     * @param id of the movie for which we want to find all its genres
     * @return SortedSet of Genre
     * @see Genre
     */
    public SortedSet<Genre> getGenresByFilmId(Long id) {
        String sql = "SELECT GENRES.* FROM FILM_GENRE " +
                "LEFT JOIN GENRES ON FILM_GENRE.GENRE_ID = GENRES.GENRE_ID WHERE film_id=?";
        return new TreeSet<>(jdbcTemplate.query(sql, genreDbStorage::mapRowToGenre, id));
    }
}