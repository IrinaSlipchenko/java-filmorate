package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.db.FeedDbStorage;
import ru.yandex.practicum.filmorate.storage.db.LikesDbStorage;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
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
        if(likesDbStorage.containsLike(filmID, userID)) {
            filmStorage.update(film);
            feedDbStorage.add(Feed.builder()
                    .timestamp(new Timestamp(System.currentTimeMillis()).getTime())
                    .userId(userID)
                    .eventType("LIKE")
                    .operation("UPDATE")
                    .entityId(filmID)
                    .build());
        }else {
            filmStorage.update(film);
            feedDbStorage.add(Feed.builder()
                    .timestamp(new Timestamp(System.currentTimeMillis()).getTime())
                    .userId(userID)
                    .eventType("LIKE")
                    .operation("ADD")
                    .entityId(filmID)
                    .build());
        }
        return film;
    }

    public Film deleteLikeFilm(Long id, Long userId) {
        Film film = filmStorage.findFilmById(id);
        User user = userStorage.findUserById(userId);
        film.getLikes().remove(user.getId());
        filmStorage.update(film);
        feedDbStorage.add(Feed.builder()
                .timestamp(new Timestamp(System.currentTimeMillis()).getTime())
                .userId(userId)
                .eventType("LIKE")
                .operation("REMOVE")
                .entityId(id)
                .build());
        return film;
    }

    public List<Film> getPopularFilms(int count, Integer genreId, Integer year) {
        return filmStorage.getPopularFilms(count, genreId, year);
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);
        return filmStorage.getCommonFilms(user.getId(),friend.getId());
    }
}
