package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NoSuchIdException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Class is provides database functionalities for MPA rating of the film.
 */
@Component
@RequiredArgsConstructor
public class MpaDBStorage {
    /**
     * @see JdbcTemplate
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * Get all MPA rating
     *
     * @return a List of MPA rating as MPA Object
     * @see Mpa
     * @see Film
     */
    public List<Mpa> findAll() {
        String sql = "SELECT * FROM RATING_MPA";
        return jdbcTemplate.query(sql, this::mapRowToMpa);
    }

    /**
     * Mapping a query result to Mpa object
     *
     * @param rs the specified as identifier of ResultSet
     * @param i  the specified as number of record from ResultSet
     * @return the mpa of film as Mpa object
     * @see Feed
     */
    private Mpa mapRowToMpa(ResultSet rs, int i) throws SQLException {
        return Mpa.builder()
                .id(rs.getInt("mpa_id"))
                .name(rs.getString("mpa_name"))
                .description(rs.getString("mpa_description"))
                .build();
    }

    /**
     * Get MPA rating by his id
     *
     * @param mpaID the specified as identifier of the mpa rating
     * @return the MPA rating as Mpa Object
     * @throws NoSuchIdException if the mpa rating not found in the storage by ID
     * @see Mpa
     * @see Film
     */
    public Mpa findMpaById(Long mpaID) {
        String sql = "SELECT * FROM RATING_MPA WHERE mpa_id=?";
        try {
            return jdbcTemplate.queryForObject(sql, this::mapRowToMpa, mpaID);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchIdException(mpaID + " id - mpa not found");
        }
    }
}
