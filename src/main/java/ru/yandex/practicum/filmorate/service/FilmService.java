package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

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
        filmStorage.update(film);
        return film;
    }

    public Film deleteLikeFilm(Long id, Long userId) {
        Film film = filmStorage.findFilmById(id);
        User user = userStorage.findUserById(userId);
        film.getLikes().remove(user.getId());
        filmStorage.update(film);
        return film;
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);
        return filmStorage.getCommonFilms(user.getId(),friend.getId());
    }
}
