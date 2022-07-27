package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

/**
 * The class helps listen to user request at "/genres"
 */
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    /**
     * @see GenreService
     */
    private final GenreService genreService;

    /**
     * @return all genres saved in storage at the current moment
     * @see Genre
     */
    @GetMapping
    public List<Genre> findAll() {
        return genreService.findAll();
    }

    /**
     * @param id the specified identifier for the genre to be searched.
     * @return the genre saved in storage under specified identifier
     * @see Genre
     */
    @GetMapping("/{id}")
    public Genre findGenre(@PathVariable("id") Long id) {
        return genreService.findGenreById(id);
    }
}
