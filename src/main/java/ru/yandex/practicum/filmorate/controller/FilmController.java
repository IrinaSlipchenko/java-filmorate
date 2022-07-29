package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.FilmValidator;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.EnumSet;
import java.util.List;

/**
 * The class helps listen to user request at "/films"
 */
@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {

    /**
     * @see FilmService
     */
    private final FilmService filmService;

    /**
     * @see FilmValidator
     */
    private final FilmValidator filmValidator;

    /**
     * @return all films saved in storage at the current moment
     * @see Film
     */
    @GetMapping
    public List<Film> findAll() {
        return filmService.findAll();
    }

    /**
     * @param id the specified identifier for the film to be searched
     * @return the film saved in storage under specified identifier
     * @see Film
     */
    @GetMapping("/{id}")
    public Film findFilm(@PathVariable("id") Long id) {
        return filmService.findFilmById(id);
    }

    /**
     * @param film the json specified as film object with no identifier received to save in storage
     * @return film specified with identifier and saved with given parameters in storage
     * @see Film
     * @see FilmValidator
     */
    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("create film write it to the log");
        return filmService.create(filmValidator.validateAndChange(film));
    }

    /**
     * @param film the json specified as film object with existing identifier received to save in storage
     * @return film specified with identifier and saved with updated parameters in storage
     * @see Film
     * @see FilmValidator
     */
    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("update film write it to the log");
        return filmService.update(filmValidator.validateAndChange(film));
    }

    /**
     * @param id the specified identifier for the film to be deleted
     * @return film specified with identifier and deleted from storage
     * @see Film
     */
    @DeleteMapping("/{id}")
    public Film deleteFilmById(@PathVariable Long id) {
        log.info("delete film write it to the log");
        return filmService.deleteFilmById(id);
    }

    /**
     * @param id     the identifier for the film to be liked by user
     * @param userId the identifier for the user who liked film
     * @return film specified with identifier and saved with updated likes in storage
     * @see Film
     * @see User
     */
    @PutMapping("/{id}/like/{userId}")
    public Film likeFilm(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.likeFilm(id, userId);
    }

    /**
     * @param id     the identifier for the film, like for which to be deleted from storage
     * @param userId the identifier for the user who liked film
     * @return film specified with identifier and saved with updated likes in storage
     * @see Film
     * @see User
     */
    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLikeFilm(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.deleteLikeFilm(id, userId);
    }

    /**
     * @param count   the number of films to be returned, if absent then count = 10
     * @param genreId genre identifier, if present the method would return films of that genre, else: any
     * @param year    the number of year, if present the method would return films produced in that year, else: any
     * @return the most popular films sorted ascending by user likes of specified genre and produced by in pointed year
     * @see Film
     * @see Genre
     */
    @GetMapping("/popular")
    public List<Film> getPopularFilms(
            @RequestParam(required = false, value = "count", defaultValue = "10") @Positive int count,
            @RequestParam(required = false, value = "genreId") Integer genreId,
            @RequestParam(required = false, value = "year") Integer year) {
        return filmService.getPopularFilms(count, genreId, year);
    }

    /**
     * @param userId   the user identifier, whose liked films to be compared with other user to find common ones
     * @param friendId the other user identifier, whose liked films to be compared with user to find common ones
     * @return the common films liked by both users
     * @see Film
     * @see User
     */
    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam(value = "userId") Long userId
            , @RequestParam(value = "friendId") Long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    /**
     * @param text         the substring searched in films parameters
     * @param searchParams the searching parameters, could be identified as director name, film name (title)
     * @return films relevant to the searched text in certain parameters
     * @see Film
     * @see SearchParam
     */
    @GetMapping("search")
    public List<Film> searchFilms(@RequestParam(value = "query") @NotBlank String text,
                                  @RequestParam(value = "by") EnumSet<SearchParam> searchParams) {
        return filmService.searchFilms(text, searchParams);
    }

    /**
     * @param directorId the director identifier the film of which to be returned
     * @param sortBy     sorting parameter for films returned, could be specified as likes or year
     * @return the films of the specified director sorted by users likes or number of year of production
     * @see Film
     * @see Director
     * @see User
     * @see SortParam
     */
    @GetMapping("/director/{directorId}")
    public List<Film> getSortedFilmsByDirector(@PathVariable Long directorId,
                                               @RequestParam(value = "sortBy") SortParam sortBy) {
        return filmService.getSortedFilmsByDirector(directorId, sortBy);
    }

}
