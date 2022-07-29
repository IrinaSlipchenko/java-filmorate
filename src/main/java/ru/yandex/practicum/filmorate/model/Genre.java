package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

/**
 * This class describes the genre type for the film.
 *
 * @see Film
 */
@Data
@Builder
public class Genre implements Comparable<Genre> {

    /**
     * the identifier for the genre.
     * Mostly used to specify the object for the storage interactions.
     */
    private int id;

    /**
     * the name of the genre type.
     */
    private String name;

    /**
     * @param g the object to be compared. Expected to be genre.
     * @return result of comparing of identifiers for this and compared object.Could be negative, zero or positive.
     */
    @Override
    public int compareTo(Genre g) {
        return this.getId() - g.getId();
    }
}
