package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);
    private final static LocalDate EARLIEST_DATE = LocalDate.of(1895, 12, 28);

    private Map<Long, Film> films = new HashMap<>();

    private long id;

    @GetMapping
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("create");
        if(film.getReleaseDate().isBefore(EARLIEST_DATE)){
            throw new ValidationException("Wrong date");
        }
        film.setId(nextID());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film){
        if(films.containsKey(film.getId())){
            if(film.getReleaseDate().isBefore(EARLIEST_DATE)){
                throw new ValidationException("Wrong date");
            }
            films.put(film.getId(), film);
            return film;
        }
        throw new ValidationException("Film not exist");
    }



    private long nextID(){
        return ++id;
    }

    // добавить обновление фильма;

}
