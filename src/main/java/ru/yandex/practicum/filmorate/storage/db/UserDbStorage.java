package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

@Component
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    private final FriendsStorage friendsStorage;
    private final FilmStorage filmStorage;

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM USERS";
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    @Override
    public User create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert.withTableName("users").usingGeneratedKeyColumns("user_id");
        SqlParameterSource parametersU = new MapSqlParameterSource()
                .addValue("user_name", user.getName())
                .addValue("login", user.getLogin())
                .addValue("email", user.getEmail())
                .addValue("birthday", user.getBirthday());
        Number id = simpleJdbcInsert.executeAndReturnKey(parametersU);
        user.setId((Long) id);
        return user;
    }

    @Override
    public User update(User user) {
        findUserById(user.getId());
        String sql = "UPDATE users SET user_name =?," +
                "login=?, email=?," +
                "birthday=? WHERE user_id=?";
        jdbcTemplate.update(sql, user.getName(), user.getLogin(),
                user.getEmail(), user.getBirthday(), user.getId());
        friendsStorage.updateFriends(user);
        return user;
    }

    @Override
    public User findUserById(Long userId) {
        String sql = "SELECT U.*, F.friend_id FROM USERS AS U\n" +
                "LEFT JOIN FRIENDS F on U.USER_ID = F.USER_ID\n" +
                "WHERE U.USER_ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, this::mapRowToUser, userId);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundException(userId + " id - user not found");
        }
    }

    @Override
    public User deleteUserById(Long userId) {
        User user = findUserById(userId);
        String sql = "DELETE FROM users WHERE user_id=?";
        jdbcTemplate.update(sql, userId);
        return user;
    }

    @Override
    public List<User> allMyFriends(Long id) {
        final String sql = "select U.* from FRIENDS AS F " +
                "inner join USERS AS U on F.FRIEND_ID= U.USER_ID where F.USER_ID = ?";
        return jdbcTemplate.query(sql, this::mapRowToUser, id);
    }

    @Override
    public List<Film> recommendations(Long id) {
        List<Film> result = new ArrayList<>();
        Map<Long, Set<Long>> likes = getLikes();
        if (!likes.containsKey(id) || likes.size() <= 1) {
            return result;
        }
        Set<Long> targetSet = likes.get(id);
        likes.remove(id);
        int maxIntersectionSize = 0;
        Long recommendUserId = null;
        for (Map.Entry<Long, Set<Long>> uid : likes.entrySet()) {
            Set<Long> intersection = new HashSet<>(uid.getValue());
            intersection.retainAll(targetSet);
            if (intersection.equals(uid.getValue())) {
                continue;
            }
            if (intersection.size() > maxIntersectionSize) {
                maxIntersectionSize = intersection.size();
                recommendUserId = uid.getKey();
            }
        }
        if (recommendUserId != null) {
            Set<Long> set = new HashSet<>(likes.get(recommendUserId));
            set.removeAll(targetSet);
            for (Long filmId : set) {
                result.add(filmStorage.findFilmById(filmId));
            }
        }
        return result;
    }

    private Map<Long, Set<Long>> getLikes() {
        Map<Long, Set<Long>> likes = new HashMap<>();
        jdbcTemplate.query("SELECT FILM_ID, USER_ID FROM FILM_LIKES",
                (rs, rowNum) -> {
                    Long filmId = rs.getLong("FILM_ID");
                    Long userId = rs.getLong("USER_ID");
                    if (!likes.containsKey(filmId)) {
                        likes.put(filmId, new HashSet<>());
                    }
                    likes.get(filmId).add(userId);
                    return null;
                });
        return likes;
    }

    private User mapRowToUser(ResultSet rs, int i) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        Set<Long> fr = new HashSet<>();
        User user = User.builder()
                .id(rs.getLong("user_id"))
                .name(rs.getString("user_name"))
                .login(rs.getString("login"))
                .email(rs.getString("email"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
        if (rsmd.getColumnCount() > 5) {
            do {
                if (rs.getLong("friend_id") != 0) {
                    fr.add(rs.getLong("friend_id"));
                }
            } while (rs.next());
        }
        user.setFriends(fr);
        return user;
    }
}
