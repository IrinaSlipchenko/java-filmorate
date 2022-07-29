package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.db.MpaDBStorage;

import java.util.List;

/**
 * This class helps lever MpaController translated user's request for Mpa objects to inner layer of storage,
 * preventing direct access of controllers to storage and adding some business logic if needed
 *
 * @see MpaDBStorage
 */

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaDBStorage mpaDBStorage;

    /**
     * Find all Mpa in storage
     *
     * @return List of Mpa saved in storage at the current moment
     * @see Mpa
     */
    public List<Mpa> findAll() {
        return mpaDBStorage.findAll();
    }

    /**
     * Searches for a mpa by ID in storage
     *
     * @param mpaId the identifier by which we want to find the mpa in storage
     * @return Mpa the mpa object saved in storage under specified identifier
     * @see Mpa
     */
    public Mpa findMpaById(Long mpaId) {
        return mpaDBStorage.findMpaById(mpaId);
    }
}
