package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashSet;

/**
 * The class helps verify received by controllers' user json object to specified requirements of Film class
 */
@Component
public class FilmValidator {
    private final static LocalDate EARLIEST_DATE = LocalDate.of(1895, 12, 28);

    /**
     * Film object verification
     *
     * @param film object of Film class needed to verify fields: releaseDate, likes
     * @return Film object verified
     * @throws ValidationException if the date is before 12/28/1895
     * @see Film
     */
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
