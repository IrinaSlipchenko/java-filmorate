package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

/**
 * The class helps listen to user request at "/mpa"
 */
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {

    /**
     * @see MpaService
     */
    private final MpaService mpaService;

    /**
     * @return all MPA ratings saved in storage at the current moment
     * @see Mpa
     */
    @GetMapping
    public List<Mpa> findAll() {
        return mpaService.findAll();
    }

    /**
     * @param id the specified identifier for the MPA rating to be searched.
     * @return the MPA rating saved in storage under specified identifier
     * @see Mpa
     */
    @GetMapping("/{id}")
    public Mpa findMpa(@PathVariable("id") Long id) {
        return mpaService.findMpaById(id);
    }
}