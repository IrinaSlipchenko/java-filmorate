package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Class is provides database functionalities for genres.
 */

@Component
@RequiredArgsConstructor
public class GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;

    /**
     * Find all genres in storage
     *
     * @return List of Genre saved in storage at the current moment
     * @see Genre
     */
    public List<Genre> findAll() {
        String sql = "SELECT * FROM GENRES ORDER BY GENRE_ID";
        return jdbcTemplate.query(sql, this::mapRowToGenre);
    }

    /**
     * Mapping a query result to Genre object
     *
     * @param rs the specified as identifier of ResultSet
     * @param i  the specified as number of record from ResultSet
     * @return Genre object
     * @see Genre
     */

    Genre mapRowToGenre(ResultSet rs, int i) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("genre_name"))
                .build();
    }

    /**
     * Searches for a genre by ID in storage
     *
     * @param genreID the specified as identifier of the genre
     * @return Genre
     * @throws GenreNotFoundException if the genre not found in the storage by ID
     * @see Genre
     * @see Film
     */
    public Genre findGenreById(Long genreID) {
        String sql = "SELECT * FROM GENRES WHERE genre_id=?";
        try {
            return jdbcTemplate.queryForObject(sql, this::mapRowToGenre, genreID);
        } catch (EmptyResultDataAccessException e) {
            throw new GenreNotFoundException(genreID + " id - genre not found");
        }
    }
}
