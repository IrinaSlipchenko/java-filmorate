package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class Genre implements Comparable<Genre> {
    private int id;

    private String name;

    @Override
    public int compareTo(Genre g) {
        return this.getId() - g.getId();
    }
}
