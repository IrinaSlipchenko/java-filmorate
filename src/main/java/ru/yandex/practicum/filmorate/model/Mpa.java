package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

/**
 * This class describes the MPA rating for the film.
 *
 * @see Film
 */
@Data
@Builder
public class Mpa {

    /**
     * the identifier for the MPA rating.
     * Mostly used to specify the object for the storage interactions.
     */
    private int id;

    /**
     * the name of the MPA rating.
     */
    private String name;

    /**
     * the description for MPA rating, for what that type of rating stands for.
     */
    private String description;
}
