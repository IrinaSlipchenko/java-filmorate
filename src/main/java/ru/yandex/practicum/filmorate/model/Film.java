package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import ru.yandex.practicum.filmorate.service.FilmValidator;

import java.time.LocalDate;
import java.util.Set;
import java.util.SortedSet;

/**
 * This class describes the film.
 * Film parameters should meet some basic requirements
 */
@Data
@Builder
public class Film {

    /**
     * the identifier for the film.
     * Mostly used to specify the object for the storage interactions.
     */
    private Long id;

    /**
     * the name of the film. Could not be blank.
     */
    @NotBlank
    private String name;

    /**
     * the description of the film. Max length for description <= 200 chars.
     */
    @Size(max = 200)
    private String description;

    /**
     * the release date of the film. Expected to be older or equal than the first film in thw world was recorded.
     *
     * @see FilmValidator
     */
    private LocalDate releaseDate;

    /**
     * duration of the film appointed as number of minutes. Expected quantity to be positive.
     */
    @Positive
    private Integer duration;

    /**
     * the users' likes for the film mentioned to storage as users' identifiers.
     * One user can like film only at once.
     * Film can have or don't have likes.
     *
     * @see User
     */
    private Set<Long> likes;

    /**
     * the genres of the film. Film can have or don't have any genres.
     *
     * @see Genre
     */
    private SortedSet<Genre> genres;

    /**
     * the age rating nominated by MPA film association for every produced film.
     *
     * @see Mpa
     */
    @NotNull
    private Mpa mpa;

    /**
     * the director of the film. Film can have or don't have any directors.
     *
     * @see Director
     */
    private Set<Director> directors;
}
