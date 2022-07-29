package ru.yandex.practicum.filmorate.model;

import ru.yandex.practicum.filmorate.controller.FilmController;

import java.util.EnumSet;

/**
 * This class helps to define search parameters for user's request returning of films.
 *
 * @see FilmController#searchFilms(String, EnumSet)
 * @see Film
 * @see Director
 */

public enum SearchParam {

    /**
     * the director of the film, especially deals with his name
     *
     * @see Director#getName()
     */
    director,

    /**
     * the title (name) of the film
     *
     * @see Film#getName()
     */
    title
}
