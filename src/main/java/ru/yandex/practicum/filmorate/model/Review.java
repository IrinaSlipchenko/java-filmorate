package ru.yandex.practicum.filmorate.model;

import jdk.jfr.BooleanFlag;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class Review {
     private Long reviewId;
     @NotBlank(message = "Отсутсвует текст отзыва.")
     private String content;
     @NotNull(message = "Отсутствует тип отзыва : положительный или негативный.")
     private Boolean isPositive;
     @NotNull(message = "Отсутсвует ID пользователя, создавший отзыв.")
     private Long userId;
     @NotNull(message = "Отсутствует ID фильма, на который написан отзыв.")
     private Long filmId;
     private Integer useful;
}
