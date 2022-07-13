package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

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
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return film;
        }
        throw new FilmNotFoundException("Film not exist");
    }

    @Override
    public Film findFilmById(Long filmId) {
        return films.values().stream()
                .filter(f -> Objects.equals(f.getId(), filmId))
                .findFirst()
                .orElseThrow(() -> new FilmNotFoundException(String.format("%d Film not exist", filmId)));
    }


    private long nextID() {
        return ++id;
    }
}
