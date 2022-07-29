package ru.yandex.practicum.filmorate.storage.memory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SearchParam;
import ru.yandex.practicum.filmorate.model.SortParam;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    private long id;

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film create(Film film) {
        film.setId(nextID());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        Long filmId = film.getId();
        if (films.containsKey(filmId)) {
            films.put(filmId, film);
            return film;
        }
        throw new FilmNotFoundException(filmId + " id - Film not exist");
    }

    @Override
    public Film findFilmById(Long filmId) {
        if (films.containsKey(filmId)) {
            return films.get(filmId);
        }
        throw new FilmNotFoundException(filmId + " id - Film not exist");
    }

    @Override
    public Film deleteFilmById(Long filmId) {
        if (films.containsKey(filmId)) {
            Film film = films.get(filmId);
            films.remove(filmId);
            return film;
        }
        throw new FilmNotFoundException(filmId + " id - Film not exist");
    }

    @Override
    public List<Film> getPopularFilms(int count, Integer genreId, Integer year) {
        return null;
    }

    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return null;
    }

    @Override
    public List<Film> searchFilms(String text, EnumSet<SearchParam> searchParams) {
        return null;
    }

    @Override
    public List<Film> getSortedFilmsByDirector(Long directorId, SortParam sortBy) {
        return null;
    }

    private long nextID() {
        return ++id;
    }
}
