package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class is provides database functionalities for directors.
 */
@Primary
@Component
@RequiredArgsConstructor
public class DirectorDbStorageImpl implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    /**
     * Query will return all directors from storage
     *
     * @return List of Director saved in storage at the current moment
     * @see Director
     */
    @Override
    public List<Director> findAll() {
        final String sql = "SELECT * FROM DIRECTORS";
        return jdbcTemplate.query(sql, this::mapRowToDirector);
    }

    /**
     * Create director in the storage
     *
     * @param director the object we want to storage
     * @return Director object specified with identifier and saved in storage
     * @see Director
     */
    @Override
    public Director create(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert.withTableName("directors").usingGeneratedKeyColumns("director_id");
        SqlParameterSource parameter = new MapSqlParameterSource()
                .addValue("director_name", director.getName());
        Number id = simpleJdbcInsert.executeAndReturnKey(parameter);
        director.setId((Long) id);
        return director;
    }

    /**
     * Update director in the storage
     *
     * @param director the object we want to update in the storage
     * @return Director object updated parameters in storage
     * @see Director
     */
    @Override
    public Director update(Director director) {
        findDirectorById(director.getId());
        String sql = "UPDATE directors SET director_name =? " +
                "WHERE director_id=?";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        return findDirectorById(director.getId());
    }

    /**
     * Searches for a director by ID in storage
     *
     * @param directorId the specified as identifier of the director
     * @return Director object
     * @throws DirectorNotFoundException if the director not found in the storage by ID
     * @see Director
     */
    @Override
    public Director findDirectorById(Long directorId) {
        final String sql = "SELECT * FROM DIRECTORS WHERE director_id=?";
        try {
            return jdbcTemplate.queryForObject(sql, this::mapRowToDirector, directorId);
        } catch (EmptyResultDataAccessException e) {
            throw new DirectorNotFoundException(directorId + " id - director not found");
        }
    }

    /**
     * Delete director from the storage.
     *
     * @param directorId the specified as identifier of director to remove from the storage
     * @return Director object removed from storage with identifier equals directorId
     * @see Director
     */
    @Override
    public Director delete(Long directorId) {
        Director director = findDirectorById(directorId);
        final String sql = "DELETE FROM DIRECTORS WHERE director_id=?";
        jdbcTemplate.update(sql, directorId);
        return director;
    }

    /**
     * The query will return all the directors that belong to the given movie
     *
     * @param id of the movie by which to find all the directors of this movie
     * @return Set of Director object
     * @see Director
     */
    public Set<Director> getDirectorsByFilmId(Long id) {
        String sql = "SELECT DIRECTORS.* FROM FILM_DIRECTORS " +
                "LEFT JOIN DIRECTORS ON FILM_DIRECTORS.DIRECTOR_ID = DIRECTORS.DIRECTOR_ID WHERE film_id=?";
        return new HashSet<>(jdbcTemplate.query(sql, this::mapRowToDirector, id));
    }

    /**
     * Updating directors for this movie
     *
     * @param film a film that needs to be updating directors
     * @see Film
     */
    public void updateDirectors(Film film) {
        String sql = "DELETE FROM FILM_DIRECTORS WHERE FILM_ID=?";
        jdbcTemplate.update(sql, film.getId());
        String sql2 = "INSERT INTO FILM_DIRECTORS (film_id, director_id) VALUES(?,?)";
        if (film.getDirectors() != null) {
            film.getDirectors().stream()
                    .map(Director::getId)
                    .forEach(id -> jdbcTemplate.update(sql2, film.getId(), id));
        }
    }

    /**
     * Mapping a query result to Director object
     *
     * @param rs the specified as identifier of ResultSet
     * @param i  the specified as number of record from ResultSet
     * @return Director object
     * @see Director
     */
    Director mapRowToDirector(ResultSet rs, int i) throws SQLException {
        return Director.builder()
                .id(rs.getLong("director_id"))
                .name(rs.getString("director_name"))
                .build();
    }
}
