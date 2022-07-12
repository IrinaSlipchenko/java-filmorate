package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage{
    private final static LocalDate EARLIEST_DATE = LocalDate.of(1895, 12, 28);

    private Map<Long, Film> films = new HashMap<>();

    private long id;

    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    public Film create(Film film){
        if (film.getReleaseDate().isBefore(EARLIEST_DATE)) {
            throw new ValidationException("Wrong date");
        }
        film.setId(nextID());
        films.put(film.getId(), film);
        return film;
    }

    public Film update (Film film){
        if (films.containsKey(film.getId())) {
            if (film.getReleaseDate().isBefore(EARLIEST_DATE)) {
                throw new ValidationException("Wrong date");
            }
            films.put(film.getId(), film);
            return film;
        }
        throw new FilmNotFoundException("Film not exist");
    }

    private long nextID() {
        return ++id;
    }
}
