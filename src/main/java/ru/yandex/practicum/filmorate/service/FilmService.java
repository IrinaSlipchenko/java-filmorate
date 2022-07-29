package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SearchParam;
import ru.yandex.practicum.filmorate.model.SortParam;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.impl.FeedDbStorageImpl;
import ru.yandex.practicum.filmorate.storage.impl.LikesDbStorageImpl;

import java.util.EnumSet;
import java.util.List;

import static ru.yandex.practicum.filmorate.model.feedEnum.OperationType.ADD;
import static ru.yandex.practicum.filmorate.model.feedEnum.OperationType.REMOVE;

/**
 * This class helps lever FilmController translated user's request for Film objects to inner layer of storage,
 * preventing direct access of controllers to storage and adding some business logic if needed
 *
 * @see FilmController
 * @see FilmStorage
 * @see UserStorage
 * @see FeedDbStorageImpl
 * @see LikesDbStorageImpl
 */

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FeedDbStorageImpl feedDbStorage;
    private final LikesDbStorageImpl likesDbStorage;

    /**
     * Find all films in storage
     *
     * @return List of Film saved in storage at the current moment
     * @see Film
     */
    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    /**
     * Create movie in the storage
     *
     * @param film the object we want to storage
     * @return Film object specified with identifier and saved in storage
     * @see Film
     */
    public Film create(Film film) {
        return filmStorage.create(film);
    }

    /**
     * Update movie in the storage
     *
     * @param film the object we want to update in the storage
     * @return Film object updated parameters in storage
     * @see Film
     */
    public Film update(Film film) {
        return filmStorage.update(film);
    }

    /**
     * Searches for a movie by ID in storage
     *
     * @param filmID the identifier by which we want to find the movie in storage
     * @return Film the film object saved in storage under specified identifier
     * @see Film
     */
    public Film findFilmById(Long filmID) {
        return filmStorage.findFilmById(filmID);
    }

    /**
     * Removes a movie by ID from storage
     *
     * @param filmID the identifier by which we want to delete the movie in storage
     * @return Film the film object delete in storage under specified identifier
     * @see Film
     */
    public Film deleteFilmById(Long filmID) {
        return filmStorage.deleteFilmById(filmID);
    }

    /**
     * Give the movie a like
     *
     * @param filmID id of the movie to like
     * @param userID id of the user who likes the movie
     * @return Film with likes
     * @see Film
     */
    public Film likeFilm(Long filmID, Long userID) {
        Film film = filmStorage.findFilmById(filmID);
        User user = userStorage.findUserById(userID);
        film.getLikes().add(user.getId());
        if (!likesDbStorage.containsLike(filmID, userID)) {
            filmStorage.update(film);
            feedDbStorage.addLike(userID, ADD, filmID);
        } else filmStorage.update(film);
        return film;
    }

    /**
     * Remove the like from the movie
     *
     * @param id     film id
     * @param userId user id
     * @return Film
     * @see Film
     */
    public Film deleteLikeFilm(Long id, Long userId) {
        Film film = filmStorage.findFilmById(id);
        User user = userStorage.findUserById(userId);
        film.getLikes().remove(user.getId());
        filmStorage.update(film);
        feedDbStorage.addLike(userId, REMOVE, id);
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
    public List<Film> getPopularFilms(int count, Integer genreId, Integer year) {
        return filmStorage.getPopularFilms(count, genreId, year);
    }

    /**
     * Return common movies by two users
     *
     * @param userId   id first user
     * @param friendId id second user
     * @return List of Film
     * @see Film
     */
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);
        return filmStorage.getCommonFilms(user.getId(), friend.getId());
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
    public List<Film> searchFilms(String text, EnumSet<SearchParam> searchParams) {
        return filmStorage.searchFilms(text, searchParams);
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
    public List<Film> getSortedFilmsByDirector(Long directorId, SortParam sortBy) {
        return filmStorage.getSortedFilmsByDirector(directorId, sortBy);
    }
}
