package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film findFilmById (Long filmID){
        return filmStorage.findFilmById(filmID);
    }

    public Film likeFilm(Long filmID, Long userID) {
        Film film = filmStorage.findFilmById(filmID);
        User user = userStorage.findUserById(userID);
        if (film.getLikes() != null){
            film.getLikes().add(user.getId());
        }else {
            Set<Long> likesSet = new HashSet<>();
            likesSet.add(user.getId());
            film.setLikes(likesSet);
        }
        return filmStorage.findFilmById(filmID);
    }

    public Film deleteLikeFilm(Long id, Long userId) {
        Film film = filmStorage.findFilmById(id);
        User user = userStorage.findUserById(userId);
        if (film.getLikes() != null){
            film.getLikes().remove(user.getId());
        }
        return filmStorage.findFilmById(id);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.findAll().stream()
                .sorted((o1, o2) -> o2.getLikes().size() - o1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());

    }
}
