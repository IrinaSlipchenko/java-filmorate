package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RatingMPA {
    private int ratingId;
    private String ratingName;
    private String ratingDescription;
}
