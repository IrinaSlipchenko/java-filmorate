package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.SearchParam;
import ru.yandex.practicum.filmorate.model.SortParam;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.FilmValidator;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.EnumSet;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;
    private final FilmValidator filmValidator;

    @GetMapping
    public List<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film findFilm(@PathVariable("id") Long id) {
        return filmService.findFilmById(id);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("create film write it to the log");
        return filmService.create(filmValidator.validateAndChange(film));
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("update film write it to the log");
        return filmService.update(filmValidator.validateAndChange(film));
    }

    @DeleteMapping("/{id}")
    public Film deleteFilmById(@PathVariable Long id) {
        log.info("delete film write it to the log");
        return filmService.deleteFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film likeFilm(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.likeFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLikeFilm(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.deleteLikeFilm(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false, value = "count", defaultValue = "10") @Positive int count,
                                      @RequestParam(required = false, value = "genreId") Integer genreId,
                                      @RequestParam(required = false, value = "year") Integer year) {
        return filmService.getPopularFilms(count, genreId, year);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(@RequestParam(value = "userId") Long userId
            , @RequestParam(value = "friendId") Long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("search")
    public List<Film> searchFilms(@RequestParam(value = "query") @NotBlank String text,
                                  @RequestParam(value = "by") EnumSet<SearchParam> searchParams) {
        return filmService.searchFilms(text, searchParams);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getSortedFilmsByDirector(@PathVariable Long directorId,
                                               @RequestParam(value = "sortBy") SortParam sortBy) {
        return filmService.getSortedFilmsByDirector(directorId, sortBy);
    }
}
