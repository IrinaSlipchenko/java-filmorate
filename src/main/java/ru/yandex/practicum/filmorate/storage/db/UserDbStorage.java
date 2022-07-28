package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class is provides database functionalities for users.
 */
@Component
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    /**
     * @see JdbcTemplate
     */
    private final JdbcTemplate jdbcTemplate;

    /**
     * @see FriendsStorage
     */
    private final FriendsStorage friendsStorage;

    /**
     * Find and returns all users in the storage
     *
     * @return a List or users as Users objects which contains in storage
     * @see User
     */
    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM USERS";
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    /**
     * Add user to storage
     *
     * @param user the specified as User object without identifier to be saved in the storage
     * @return the user as User object saved in storage with unique identifier
     * @see User
     */
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

    /**
     * Update user data in the storage
     *
     * @param user the specified as User object with identifier to be updated in the storage
     * @return the user as User object updated in the storage
     * @see User
     * @see UserStorage
     */
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

    /**
     * Find and return user data from the storage
     *
     * @param userId the specified as identifier of user to get from the storage
     * @return the user as User object saved in storage with identifier equals userID
     * @throws UserNotFoundException if user not found by ib
     * @see User
     */
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

    /**
     * Delete user from the storage
     *
     * @param userId the specified as identifier of user to remove from the storage
     * @return the user as User object removed from storage with identifier equals userId
     * @see User
     */
    @Override
    public User deleteUserById(Long userId) {
        User user = findUserById(userId);
        String sql = "DELETE FROM users WHERE user_id=?";
        jdbcTemplate.update(sql, userId);
        return user;
    }

    /**
     * Returns all friends of user.
     *
     * @param id the specified as identifier of user
     * @return a List containing the friends as User Objects
     * @see User
     */
    @Override
    public List<User> allMyFriends(Long id) {
        final String sql = "select U.* from FRIENDS AS F " +
                "inner join USERS AS U on F.FRIEND_ID= U.USER_ID where F.USER_ID = ?";
        return jdbcTemplate.query(sql, this::mapRowToUser, id);
    }

    /**
     * Return does the database contain the user by id
     *
     * @param userId the specified as identifier of the user
     * @return does database contain the user by id
     * @see Review
     */
    public Boolean containsIdUser(Long userId) {
        final String sql = "SELECT user_id FROM users WHERE user_id = ?";
        return jdbcTemplate.queryForList(sql, userId).size() > 0;
    }

    /**
     * Mapping a query result to User object
     *
     * @param rs the specified as identifier of ResultSet
     * @param i  the specified as number of record from ResultSet
     * @return user as User object with friends
     * @see User
     */
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
