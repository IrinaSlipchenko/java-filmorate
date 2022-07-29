package ru.yandex.practicum.filmorate.model;

import ru.yandex.practicum.filmorate.controller.FilmController;

/**
 * This class helps to define sort parameters for user's request returning of films.
 *
 * @see FilmController#getSortedFilmsByDirector(Long, SortParam)
 * @see Film
 * @see Director
 */
public enum SortParam {

    /**
     * year of the release date of the film
     *
     * @see Film#getReleaseDate()
     */
    year,

    /**
     * count of likes for the film
     *
     * @see Film#getLikes()
     */
    likes
}
