package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashSet;

@Component
public class FilmValidator {
    private final static LocalDate EARLIEST_DATE = LocalDate.of(1895, 12, 28);

    public Film validateAndChange(Film film) {

        if (film.getReleaseDate().isBefore(EARLIEST_DATE)) {
            throw new ValidationException("Wrong date");
        }
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        return film;
    }
}
