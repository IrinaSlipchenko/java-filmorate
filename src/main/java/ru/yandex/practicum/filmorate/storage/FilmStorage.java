package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    List<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    Film findFilmById(Long filmId);

    Film delete(Long filmId);

    List<Film> getPopularFilms(int count);
}
