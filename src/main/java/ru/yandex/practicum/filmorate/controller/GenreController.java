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
 *
 */
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    /**
     *
     */
    private final GenreService genreService;

    /**
     * @return
     */
    @GetMapping
    public List<Genre> findAll() {
        return genreService.findAll();
    }

    /**
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Genre findGenre(@PathVariable("id") Long id) {
        return genreService.findGenreById(id);
    }
}
