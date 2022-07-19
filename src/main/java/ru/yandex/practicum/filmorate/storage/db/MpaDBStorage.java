package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MpaDBStorage {
    private final JdbcTemplate jdbcTemplate;

    public List<Mpa> findAll() {
        String sql = "SELECT * FROM RATING_MPA";
        return jdbcTemplate.query(sql, this::mapRowToMpa);
    }

    private Mpa mapRowToMpa(ResultSet rs, int i) throws SQLException {
        return Mpa.builder()
                .id(rs.getInt("mpa_id"))
                .name(rs.getString("mpa_name"))
                .description(rs.getString("mpa_description"))
                .build();
    }

    public Mpa findMpaById(Long mpaID) {
        String sql = "SELECT * FROM RATING_MPA WHERE mpa_id=?";
        try {
            return jdbcTemplate.queryForObject(sql, this::mapRowToMpa, mpaID);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException(mpaID + " id - mpa not found");
        }
    }
}
