package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.SearchParam;
import ru.yandex.practicum.filmorate.model.SortParam;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class is provides database functionalities for films.
 *
 * @see JdbcTemplate
 * @see FilmGenreDbStorageImpl
 * @see LikesDbStorageImpl
 * @see DirectorDbStorageImpl
 */
@Primary
@Component
@RequiredArgsConstructor
public class FilmDbStorageImpl implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    private final FilmGenreDbStorageImpl fgStorage;

    private final LikesDbStorageImpl likesStorage;

    private final DirectorDbStorageImpl directorDbStorage;

    /**
     * Query will return all films from storage
     *
     * @return List of Film saved in storage at the current moment
     * @see Film
     */
    @Override
    public List<Film> findAll() {
        String sql = "SELECT F.*, R.MPA_NAME, R.MPA_DESCRIPTION FROM FILMS F\n" +
                "LEFT JOIN RATING_MPA R ON F.RATING_MPA_ID=R.MPA_ID";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    /**
     * Create film in the storage
     *
     * @param film the object we want to storage
     * @return Film object specified with identifier and saved in storage
     * @see Film
     */
    @Override
    public Film create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        simpleJdbcInsert.withTableName("films").usingGeneratedKeyColumns("film_id");
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("film_name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("release_date", film.getReleaseDate())
                .addValue("duration", film.getDuration())
                .addValue("rating_mpa_id", film.getMpa().getId());

        Number id = simpleJdbcInsert.executeAndReturnKey(parameters);
        film.setId((Long) id);
        fgStorage.updateGenres(film);
        directorDbStorage.updateDirectors(film);
        return film;
    }

    /**
     * Update film in the storage
     *
     * @param film the object we want to update in the storage
     * @return Film object updated parameters in storage
     * @see Film
     */
    @Override
    public Film update(Film film) {
        findFilmById(film.getId());
        String sql = "UPDATE films SET film_name =?," +
                "description=?, release_date=?," +
                "duration=?, rating_mpa_id=? WHERE film_id=?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), film.getId());
        fgStorage.updateGenres(film);
        likesStorage.updateLikes(film);
        directorDbStorage.updateDirectors(film);
        return film;
    }

    /**
     * Searches for a film by ID in storage
     *
     * @param filmId the identifier by which we want to find the movie in storage
     * @return Film the film object saved in storage under specified identifier
     * @see Film
     */
    @Override
    public Film findFilmById(Long filmId) {
        String sql = "SELECT F.*, R.MPA_NAME, R.MPA_DESCRIPTION FROM FILMS F\n" +
                "LEFT JOIN RATING_MPA R ON F.RATING_MPA_ID=R.MPA_ID\n" +
                "WHERE F.FILM_ID=?";
        try {
            return jdbcTemplate.queryForObject(sql, this::mapRowToFilm, filmId);
        } catch (EmptyResultDataAccessException e) {
            throw new FilmNotFoundException(filmId + " id - film not found");
        }
    }

    /**
     * Removes a movie by ID from storage
     *
     * @param filmId the identifier by which we want to delete the movie in storage
     * @return Film the film object delete in storage under specified identifier
     * @see Film
     */
    @Override
    public Film deleteFilmById(Long filmId) {
        Film film = findFilmById(filmId);
        String sql = "DELETE FROM films WHERE film_id=?";
        jdbcTemplate.update(sql, filmId);
        return film;
    }

    /**
     * Returns movies sorted by number of likes
     *
     * @param count   count of movies
     * @param genreId id genres
     * @param year    release year
     * @return List of Film
     * @see Film
     */
    @Override
    public List<Film> getPopularFilms(int count, Integer genreId, Integer year) {
        final String sql = "SELECT F.FILM_ID, count(USER_ID) LIKES\n" +
                "FROM FILM_LIKES FL\n" +
                "RIGHT JOIN FILMS F on FL.FILM_ID = F.FILM_ID\n" +
                "WHERE (? is null OR EXISTS (SELECT 1 FROM FILM_GENRE FG WHERE FG.FILM_ID = F.FILM_ID AND FG.GENRE_ID = ?)) \n" +
                "  AND (? is null OR EXTRACT(YEAR FROM F.RELEASE_DATE) = ?) \n" +
                "GROUP BY F.FILM_ID\n" +
                "ORDER BY LIKES DESC\n" +
                "LIMIT ?";

        List<Long> idList = jdbcTemplate.query(sql, (rs, i) -> rs.getLong("film_id"), genreId, genreId, year, year, count);
        return idList.stream().map(this::findFilmById).collect(Collectors.toList());
    }

    /**
     * Return common movies by two users
     *
     * @param userId   id first user
     * @param friendId id second user
     * @return List of Film
     * @see Film
     */
    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        final String sql = "SELECT F.FILM_ID, count(USER_ID) LIKES\n" +
                "FROM FILM_LIKES FL\n" +
                "RIGHT JOIN FILMS F on FL.FILM_ID = F.FILM_ID\n" +
                "WHERE F.FILM_ID IN (" +
                "SELECT FL2.FILM_ID FROM FILM_LIKES FL2 WHERE FL2.USER_ID = ? AND FL2.FILM_ID IN(" +
                "SELECT FL3.FILM_ID FROM FILM_LIKES FL3 WHERE FL3.USER_ID = ?" +
                ") GROUP BY FL2.FILM_ID" +
                ") " +
                "GROUP BY F.FILM_ID\n" +
                "ORDER BY LIKES DESC";
        System.out.println("userId: " + userId);
        System.out.println("friendId: " + friendId);
        List<Long> idList = jdbcTemplate.query(sql, (rs, i) -> rs.getLong("film_id"), userId, friendId);

        return idList.stream().map(this::findFilmById).collect(Collectors.toList());
    }

    /**
     * Search for a movie by director or title
     *
     * @param text         keyword
     * @param searchParams search parameters
     * @return List of Film
     * @see Film
     * @see SearchParam
     */
    @Override
    public List<Film> searchFilms(String text, EnumSet<SearchParam> searchParams) {
        final String sqlAllSearch = "SELECT F.*, R.MPA_NAME, R.MPA_DESCRIPTION FROM FILMS F\n" +
                "                LEFT JOIN RATING_MPA R ON F.RATING_MPA_ID=R.MPA_ID\n" +
                "                LEFT JOIN FILM_DIRECTORS FD on F.FILM_ID = FD.FILM_ID\n" +
                "                LEFT JOIN DIRECTORS D on FD.DIRECTOR_ID = D.DIRECTOR_ID\n" +
                "                LEFT JOIN FILM_LIKES FL on F.FILM_ID = FL.FILM_ID\n" +
                "                WHERE D.DIRECTOR_NAME ILIKE '%'||?||'%'\n" +
                "                OR F.FILM_NAME ILIKE '%'||?||'%'\n" +
                "                GROUP BY F.FILM_ID\n" +
                "                ORDER BY COUNT(FL.USER_ID) DESC";
        final String sqlSearchByDirector = "SELECT F.*, R.MPA_NAME, R.MPA_DESCRIPTION FROM FILMS F\n" +
                "                LEFT JOIN RATING_MPA R ON F.RATING_MPA_ID=R.MPA_ID\n" +
                "                LEFT JOIN FILM_DIRECTORS FD on F.FILM_ID = FD.FILM_ID\n" +
                "                LEFT JOIN DIRECTORS D on FD.DIRECTOR_ID = D.DIRECTOR_ID\n" +
                "                LEFT JOIN FILM_LIKES FL on F.FILM_ID = FL.FILM_ID\n" +
                "                WHERE D.DIRECTOR_NAME ILIKE '%'||?||'%'\n" +
                "                GROUP BY F.FILM_ID\n" +
                "                ORDER BY COUNT(FL.USER_ID) DESC";
        final String sqlSearchByFilmTitle = "SELECT F.*, R.MPA_NAME, R.MPA_DESCRIPTION FROM FILMS F\n" +
                "                LEFT JOIN RATING_MPA R ON F.RATING_MPA_ID=R.MPA_ID\n" +
                "                LEFT JOIN FILM_DIRECTORS FD on F.FILM_ID = FD.FILM_ID\n" +
                "                LEFT JOIN DIRECTORS D on FD.DIRECTOR_ID = D.DIRECTOR_ID\n" +
                "                LEFT JOIN FILM_LIKES FL on F.FILM_ID = FL.FILM_ID\n" +
                "                WHERE F.FILM_NAME ILIKE '%'||?||'%'\n" +
                "                GROUP BY F.FILM_ID\n" +
                "                ORDER BY COUNT(FL.USER_ID) DESC";

        return (searchParams.contains(SearchParam.title) && searchParams.contains(SearchParam.director))
                ?
                jdbcTemplate.query(sqlAllSearch, this::mapRowToFilm, text, text)
                :
                searchParams.contains(SearchParam.director)
                        ?
                        jdbcTemplate.query(sqlSearchByDirector, this::mapRowToFilm, text)
                        :
                        jdbcTemplate.query(sqlSearchByFilmTitle, this::mapRowToFilm, text);
    }

    /**
     * Validate film id
     *
     * @param filmId id of film
     * @return true or false
     */
    public Boolean containsIdFilm(Long filmId) {
        final String sql = "SELECT film_id FROM films WHERE film_id = ?";
        return jdbcTemplate.queryForList(sql, filmId).size() > 0;
    }

    /**
     * Mapping a query result to Film object
     *
     * @param rs the specified as identifier of ResultSet
     * @param i  the specified as number of record from ResultSet
     * @return Film object
     * @see Film
     * @see Mpa
     */
    private Film mapRowToFilm(ResultSet rs, int i) throws SQLException {
        Mpa mpa = Mpa.builder()
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
                .mpa(mpa)
                .build();
        film.setLikes(likesStorage.getLikesByFilmId(film.getId()));
        film.setGenres(fgStorage.getGenresByFilmId(film.getId()));
        film.setDirectors(directorDbStorage.getDirectorsByFilmId(film.getId()));
        return film;
    }

    /**
     * Find movies of given director, sorted by years or likes
     *
     * @param directorId the identifier of director
     * @param sortBy     parameter of sorting
     * @return List of Film
     * @see Film
     * @see SortParam
     */
    @Override
    public List<Film> getSortedFilmsByDirector(Long directorId, SortParam sortBy) {
        directorDbStorage.findDirectorById(directorId);

        final String sqlYear = "SELECT F.*, R.MPA_NAME, R.MPA_DESCRIPTION FROM FILMS F\n" +
                "                LEFT JOIN RATING_MPA R ON F.RATING_MPA_ID=R.MPA_ID\n" +
                "                LEFT JOIN FILM_DIRECTORS FD on F.FILM_ID = FD.FILM_ID\n" +
                "                WHERE FD.DIRECTOR_ID=?\n" +
                "                ORDER BY EXTRACT(year FROM RELEASE_DATE)";

        final String sqlLikes = "SELECT F.*, R.MPA_NAME, R.MPA_DESCRIPTION FROM FILMS F\n" +
                "                LEFT JOIN RATING_MPA R ON F.RATING_MPA_ID=R.MPA_ID\n" +
                "                LEFT JOIN FILM_DIRECTORS FD on F.FILM_ID = FD.FILM_ID\n" +
                "                LEFT JOIN FILM_LIKES FL on F.FILM_ID = FL.FILM_ID\n" +
                "                WHERE FD.DIRECTOR_ID=?\n" +
                "                GROUP BY F.FILM_ID\n" +
                "                ORDER BY COUNT(USER_ID) DESC";

        return sortBy.equals(SortParam.likes)
                ?
                jdbcTemplate.query(sqlLikes, this::mapRowToFilm, directorId)
                :
                jdbcTemplate.query(sqlYear, this::mapRowToFilm, directorId);
    }
}
