package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import javax.validation.constraints.NotBlank;

/**
 * This class describes the director of the films.
 *
 * @see DirectorStorage
 * @see Film
 */
@Data
@Builder
public class Director {
    /**
     * the identifier for the director.
     * Mostly used to specify the object for the storage interactions.
     */
    private Long id;

    /**
     * the name of the director of the film. Could not be blank.
     */
    @NotBlank
    private String name;
}
