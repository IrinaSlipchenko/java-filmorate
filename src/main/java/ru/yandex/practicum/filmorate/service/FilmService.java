package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SortParam;
import ru.yandex.practicum.filmorate.model.User;
import static ru.yandex.practicum.filmorate.model.feedEnum.OperationType.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.db.FeedDbStorage;
import ru.yandex.practicum.filmorate.storage.db.LikesDbStorage;

import java.util.List;



@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FeedDbStorage feedDbStorage;
    private final LikesDbStorage likesDbStorage;

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film findFilmById(Long filmID) {
        return filmStorage.findFilmById(filmID);
    }

    public Film deleteFilmById(Long filmID) {
        return filmStorage.deleteFilmById(filmID);
    }

    public Film likeFilm(Long filmID, Long userID) {
        Film film = filmStorage.findFilmById(filmID);
        User user = userStorage.findUserById(userID);
        film.getLikes().add(user.getId());
        if(!likesDbStorage.containsLike(filmID, userID)) {
            filmStorage.update(film);
            feedDbStorage.addLike(userID, ADD, filmID );
        }
        else filmStorage.update(film);
        return film;
    }

    public Film deleteLikeFilm(Long id, Long userId) {
        Film film = filmStorage.findFilmById(id);
        User user = userStorage.findUserById(userId);
        film.getLikes().remove(user.getId());
        filmStorage.update(film);
        feedDbStorage.addLike(userId, REMOVE, id );
        return film;
    }

    public List<Film> getPopularFilms(int count, Integer genreId, Integer year) {
        return filmStorage.getPopularFilms(count, genreId, year);
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);
        return filmStorage.getCommonFilms(user.getId(), friend.getId());
    }

    public List<Film> getSortedFilmsByDirector(Long directorId, SortParam sortBy) {
        return filmStorage.getSortedFilmsByDirector(directorId, sortBy);
    }
}
