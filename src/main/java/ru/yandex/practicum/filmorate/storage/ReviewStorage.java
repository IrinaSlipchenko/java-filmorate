package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review add(Review review);

    Review get(Long reviewId);

    Review update(Review review);

    Review delete(Long reviewId);

    List<Long> getAllByFilmId(Long filmId, Integer count);

    List<Long> getAll(Integer count);

    Boolean containsIdReview(Long reviewId);

    Boolean addReaction(Long id, Long userId, Boolean isUseful);

    Boolean deleteReaction(Long id, Long userId, Boolean isUseful);
}
