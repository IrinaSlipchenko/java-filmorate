package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.FilmValidator;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;
    private final FilmValidator filmValidator;

    @Autowired
    public FilmController(FilmService filmService, FilmValidator filmValidator) {
        this.filmService = filmService;
        this.filmValidator = filmValidator;
    }

    @GetMapping
    public List<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{filmID}")
    public Film findFilm(@PathVariable("filmID") Long filmID) {
        return filmService.findFilmById(filmID);
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

    @PutMapping("/{id}/like/{userId}")
    public Film likeFilm(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.likeFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLikeFilm(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.deleteLikeFilm(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(value = "count", defaultValue = "10") @Positive int count) {
        return filmService.getPopularFilms(count);
    }


}
