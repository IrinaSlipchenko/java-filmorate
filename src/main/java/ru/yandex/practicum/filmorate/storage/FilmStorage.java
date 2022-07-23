package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SortParam;

import java.util.List;

public interface FilmStorage {

    List<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    Film findFilmById(Long filmId);

    Film deleteFilmById(Long filmId);

    List<Film> getPopularFilms(int count, Integer genreId, Integer year);

    List<Film> getCommonFilms(Long userId, Long friendId);

    List<Film> getSortedFilmsByDirector(Long directorId, SortParam sortBy);
}
