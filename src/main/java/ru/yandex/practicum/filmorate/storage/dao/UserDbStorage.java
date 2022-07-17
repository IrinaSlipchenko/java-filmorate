package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@RequiredArgsConstructor  // Второй способ внедрить зависимости в конструктор
@Component
@Primary     //  Второй способ, использовать @Primary

public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> findAll() {
        String sql = "SELECT U.*, F.friend_id FROM USERS AS U\n" +
                "LEFT JOIN FRIENDS F on U.USER_ID = F.USER_ID\n";
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
        updateFriends(user);
        return user;
    }

    private void updateFriends(User user) {

        String sql = "DELETE FROM friends WHERE user_id=?";
        String sql2 = "INSERT INTO friends (user_id, friend_id) VALUES(?,?)";
        jdbcTemplate.update(sql, user.getId());
        user.getFriends().forEach(fId -> jdbcTemplate.update(sql2, user.getId(), fId));
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
    public User delete(Long userId) {
        User user = findUserById(userId);
        String sql = "DELETE FROM users WHERE user_id=?";
        jdbcTemplate.update(sql, userId);
        return user;
    }

    private User mapRowToUser(ResultSet rs, int i) throws SQLException {
        Set<Long> fr = new HashSet<>();
        User user = User.builder()
                .id(rs.getLong("user_id"))
                .name(rs.getString("user_name"))
                .login(rs.getString("login"))
                .email(rs.getString("email"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
        do {
            if (rs.getLong("friend_id") == 0) {
                break;
            }
            fr.add(rs.getLong("friend_id"));
        } while (rs.next());

        user.setFriends(fr);
        return user;
    }
}
