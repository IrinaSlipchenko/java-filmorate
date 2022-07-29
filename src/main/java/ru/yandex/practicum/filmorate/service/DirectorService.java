package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.DirectorController;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

/**
 * This class helps lever DirectorController translated user's request for Director objects to inner layer of storage,
 * preventing direct access of controllers to storage and adding some business logic if needed
 *
 * @see DirectorController
 */
@Service
@RequiredArgsConstructor
public class DirectorService {

    /**
     * @see DirectorStorage
     */
    private final DirectorStorage directorStorage;

    /**
     * @return all directors saved in storage at the current moment
     * @see Director
     */
    public List<Director> findAll() {
        return directorStorage.findAll();
    }

    /**
     * @param director director object with no identifier received to save in storage
     * @return director object specified with identifier and saved with given parameters in storage
     * @see Director
     */
    public Director create(Director director) {
        return directorStorage.create(director);
    }

    /**
     * @param director director object with existing identifier received to save in storage
     * @return director object specified with identifier and saved with updated parameters in storage
     * @see Director
     */
    public Director update(Director director) {
        return directorStorage.update(director);
    }

    /**
     * @param directorID the specified identifier for the director to be searched
     * @return the director object saved in storage under specified identifier
     * @see Director
     */
    public Director findDirectorById(Long directorID) {
        return directorStorage.findDirectorById(directorID);
    }

    /**
     * @param directorID the specified identifier for the director to be deleted
     * @return director object specified with identifier and deleted from storage
     * @see Director
     */
    public Director deleteDirectorById(Long directorID) {
        return directorStorage.delete(directorID);
    }
}
