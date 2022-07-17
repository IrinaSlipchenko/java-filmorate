package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RatingMPA {
    private int id;
    private String name;
    private String description;
}
