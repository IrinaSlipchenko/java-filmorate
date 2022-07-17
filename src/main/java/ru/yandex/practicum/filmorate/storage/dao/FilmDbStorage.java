package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


@Slf4j
@RequiredArgsConstructor  // Первый способ, использовать @Autowired

@Component("db") // Первый способ, использовать @Qualifier

public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> findAll() {
        String sql = "SELECT F.*, G.*, R.MPA_NAME, R.MPA_DESCRIPTION FROM FILMS F\n" +
                "LEFT JOIN FILM_GENRE FG ON F.FILM_ID=FG.FILM_ID\n" +
                "LEFT JOIN GENRES G ON FG.GENRE_ID=G.GENRE_ID\n" +
                "LEFT JOIN RATING_MPA R ON F.RATING_MPA_ID=R.MPA_ID";

        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert.withTableName("films").usingGeneratedKeyColumns("film_id");
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("film_name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("release_date", film.getReleaseDate())
                .addValue("duration", film.getDuration())
                .addValue("rating_mpa_id", film.getRatingMPA().getId());
        Number id = simpleJdbcInsert.executeAndReturnKey(parameters);
        film.setId((Long) id);
        updateGenres(film);
        return film;
    }

    private void updateGenres(Film film) {
        if (film.getGenres() == null) {
            film.setGenres(new HashSet<>());
        }
        String sql = "DELETE FROM film_genre WHERE film_id=?";
        jdbcTemplate.update(sql, film.getId());
        String sql2 = "INSERT INTO film_genre (film_id, genre_id) VALUES(?,?)";
        film.getGenres().stream()
                .map(Genre::getGenreId)
                .forEach(id -> jdbcTemplate.update(sql2, film.getId(), id));
    }

    @Override
    public Film update(Film film) {
        findFilmById(film.getId());
        String sql = "UPDATE films SET film_name =?," +
                "description=?, release_date=?," +
                "duration=?,rating_mpa_id=? WHERE film_id=?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(),
                film.getRatingMPA().getId(), film.getId());
        updateGenres(film);
        updateLikes(film);
        return findFilmById(film.getId());
    }

    private void updateLikes(Film film) {
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        String sql = "DELETE FROM FILM_LIKES WHERE film_id=?";
        jdbcTemplate.update(sql, film.getId());
        String sql2 = "INSERT INTO FILM_LIKES (film_id, user_id) VALUES(?,?)";
        film.getLikes().forEach(id -> jdbcTemplate.update(sql2, film.getId(), id));
    }

    @Override
    public Film findFilmById(Long filmId) {
        String sql = "SELECT F.*, G.*, R.MPA_NAME, R.MPA_DESCRIPTION FROM FILMS F\n" +
                "LEFT JOIN FILM_GENRE FG ON F.FILM_ID=FG.FILM_ID\n" +
                "LEFT JOIN GENRES G ON FG.GENRE_ID=G.GENRE_ID\n" +
                "LEFT JOIN RATING_MPA R ON F.RATING_MPA_ID=R.MPA_ID\n" +
                "WHERE F.FILM_ID=?";
        try {
            return jdbcTemplate.queryForObject(sql, this::mapRowToFilm, filmId);
        } catch (EmptyResultDataAccessException e) {
            throw new FilmNotFoundException(filmId + " id - film not found");
        }

    }

    private Film mapRowToFilm(ResultSet rs, int i) throws SQLException {
        Set<Genre> genres = new TreeSet<>(Comparator.comparingInt(Genre::getGenreId));

        RatingMPA mpa = RatingMPA.builder()
                .id(rs.getInt("rating_mpa_id"))
                .name(rs.getString("mpa_name"))
                .description(rs.getString("mpa_description"))
                .build();

        Film film = Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("film_name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .ratingMPA(mpa)
                .likes(getLikesByFilmId(rs.getLong("film_id")))
                .build();
        do {
            if (rs.getString("genre_name") == null) {
                break;
            }
            genres.add(Genre.builder()
                    .genreId(rs.getInt("genre_id"))
                    .genreName(rs.getString("genre_name"))
                    .build()
            );
        } while (rs.next());

        film.setLikes(getLikesByFilmId(film.getId()));
        film.setGenres(genres);
        return film;
    }

    private Set<Long> getLikesByFilmId(Long id) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id=?";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, i) -> rs.getLong("user_id"), id));
    }

    @Override
    public Film delete(Long filmId) {
        Film film = findFilmById(filmId);
        String sql = "DELETE FROM films WHERE film_id=?";
        jdbcTemplate.update(sql, filmId);
        return film;
    }
}
