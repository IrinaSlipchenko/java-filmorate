package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.db.GenreDbStorage;

import java.util.List;

/**
 * This class helps lever GenreController translated user's request for Genre objects to inner layer of storage,
 * preventing direct access of controllers to storage and adding some business logic if needed
 *
 * @see GenreDbStorage
 */
@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreDbStorage genreDbStorage;

    /**
     * Find all genres in storage
     *
     * @return List of Genre saved in storage at the current moment
     * @see Genre
     */
    public List<Genre> findAll() {
        return genreDbStorage.findAll();
    }

    /**
     * Searches for a genre by ID in storage
     *
     * @param genreID the identifier by which we want to find the genre in storage
     * @return Genre the genre object saved in storage under specified identifier
     * @see Genre
     */
    public Genre findGenreById(Long genreID) {
        return genreDbStorage.findGenreById(genreID);
    }
}
