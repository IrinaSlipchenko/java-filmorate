package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * This class describes the user's review for the film.
 *
 * @see Film
 * @see User
 */
@Data
@Builder
public class Review {

    /**
     * the identifier for the film.
     * Mostly used to specify the object for the storage interactions.
     */
    private Long reviewId;

    /**
     * the content of the review written by user. Could not be blank.
     */
    @NotBlank(message = "Отсутсвует текст отзыва.")
    private String content;

    /**
     * the type of the review. Must be either or positive (value = true) or negative (value = false).
     */
    @NotNull(message = "Отсутствует тип отзыва : положительный или негативный.")
    private Boolean isPositive;

    /**
     * the identifier of user who wrote this review
     */
    @NotNull(message = "Отсутсвует ID пользователя, создавший отзыв.")
    private Long userId;

    /**
     * the identifier of film for that this review was written by user
     */
    @NotNull(message = "Отсутствует ID фильма, на который написан отзыв.")
    private Long filmId;

    /**
     * parameter of the review describing how much review was useful to be read by other users,
     * calculated as the sum of likes (+1) and dislikes (-1) of other users for this review.
     */
    private Integer useful;
}
