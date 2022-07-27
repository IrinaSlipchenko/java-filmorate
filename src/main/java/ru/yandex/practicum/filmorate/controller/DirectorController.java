package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

/**
 * The class helps listen to user request at "/directors" .
 */
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {

    /**
     * @see DirectorService
     */
    private final DirectorService directorService;

    /**
     * @return all directors saved in storage at the current moment
     * @see Director
     */
    @GetMapping
    public List<Director> findAll() {
        return directorService.findAll();
    }

    /**
     * @param id the specified identifier for the director to be searched.
     * @return the director saved in storage under specified identifier
     * @see Director
     */
    @GetMapping("/{id}")
    public Director findDirector(@PathVariable("id") Long id) {
        return directorService.findDirectorById(id);
    }


    /**
     * @param director the json specified as director object with no identifier received to save in storage
     * @return director specified with identifier and saved with given parameters in storage
     * @see Director
     */
    @PostMapping
    public Director create(@Valid @RequestBody Director director) {
        return directorService.create(director);
    }

    /**
     * @param director the json specified as director object with existing identifier received to save in storage
     * @return director specified with identifier and saved with updated parameters in storage
     * @see Director
     */
    @PutMapping
    public Director update(@Valid @RequestBody Director director) {
        return directorService.update(director);
    }

    /**
     * @param id the specified identifier for the director to be deleted.
     * @return director specified with identifier and deleted from storage
     * @see Director
     */
    @DeleteMapping("/{id}")
    public Director deleteDirectorById(@PathVariable Long id) {
        return directorService.deleteDirectorById(id);
    }
}