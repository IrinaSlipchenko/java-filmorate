package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class Genre {
    @JsonProperty("id")
    private int genreId;
    @JsonProperty("name")
    private String genreName;
}
