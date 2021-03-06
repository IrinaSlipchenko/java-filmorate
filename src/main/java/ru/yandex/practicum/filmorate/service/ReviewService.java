package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.NoSuchIdException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.impl.*;

import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.model.feedEnum.OperationType.*;

/**
 * Class is provide business functionalities for reviews.
 */
@Service
@RequiredArgsConstructor
public class ReviewService {
    /**
     * @see ReviewDbStorageImpl
     */
    private final ReviewDbStorageImpl reviewDbStorage;
    /**
     * @see UserDbStorageImpl
     */
    private final UserDbStorageImpl userDbStorage;
    /**
     * @see FilmDbStorageImpl
     */
    private final FilmDbStorageImpl filmDbStorage;
    /**
     * @see FeedDbStorageImpl
     */
    private final FeedDbStorageImpl feedDbStorage;

    /**
     * Add review to storage. To feedStorage added an entity about event.
     *
     * @param review the specified as Review object without identifier to be saved in the storage
     * @return the review as Review object saved in storage with unique identifier
     * @throws UserNotFoundException if the user not found in the storage by ID
     * @throws FilmNotFoundException if the film not found in the storage by ID
     * @see Review
     * @see FeedDbStorageImpl
     * @see ReviewDbStorageImpl
     */
    public Review add(Review review) {
        if (!userDbStorage.containsIdUser(review.getUserId())) {
            throw new UserNotFoundException(review.getUserId() + " id - user not found");
        }
        if (!filmDbStorage.containsIdFilm(review.getFilmId())) {
            throw new FilmNotFoundException(review.getFilmId() + " id - film not found");
        }
        Review resultReview = reviewDbStorage.add(review);
        feedDbStorage.addReview(review.getUserId(), ADD, review.getReviewId());
        return resultReview;
    }

    /**
     * Update review in storage. To feedStorage added an entity about event.
     *
     * @param review the specified as Review object with identifier to be updated in the storage
     *               will be updated only review content and review type
     * @return the review as Review object updated in the storage
     * @throws NoSuchIdException if the review not found in the storage by ID
     * @see Review
     * @see FeedDbStorageImpl
     * @see ReviewDbStorageImpl
     */
    public Review update(Review review) {
        if (review.getReviewId() == null) return add(review);
        if (!reviewDbStorage.containsIdReview(review.getReviewId())) {
            throw new NoSuchIdException("?????????? ???? ID = " + review.getReviewId() + " ???? ????????????");
        }
        review.setFilmId(reviewDbStorage.get(review.getReviewId()).getFilmId());
        review.setUserId(reviewDbStorage.get(review.getReviewId()).getUserId());
        Review resultReview = reviewDbStorage.update(review);
        feedDbStorage.addReview(review.getUserId(), UPDATE, review.getReviewId());
        return resultReview;
    }

    /**
     * Get a review from storage by id
     *
     * @param reviewId the specified as identifier of review to get from the storage
     * @return the review as Review object saved in storage with identifier equals reviewId
     * @throws NoSuchIdException if the review not found in the storage by ID
     * @see Review
     */
    public Review get(Long reviewId) {
        if (!reviewDbStorage.containsIdReview(reviewId)) {
            throw new NoSuchIdException("?????????? ???? ID = " + reviewId + " ???? ????????????");
        }
        return reviewDbStorage.get(reviewId);
    }

    /**
     * Delete review from the storage. To feedStorage added an entity about event.
     *
     * @param reviewId the specified as identifier of review to remove from the storage
     * @return the review as Review object removed from storage with identifier equals reviewId
     * @throws NoSuchIdException if the review not found in the storage by ID
     * @see Review
     * @see FeedDbStorageImpl
     * @see ReviewDbStorageImpl
     */
    public Review delete(Long reviewId) {
        if (!reviewDbStorage.containsIdReview(reviewId)) {
            throw new NoSuchIdException("?????????? ???? ID = " + reviewId + " ???? ????????????");
        }
        Review resultReview = reviewDbStorage.delete(reviewId);
        feedDbStorage.addReview(resultReview.getUserId(), REMOVE, resultReview.getReviewId());
        return resultReview;
    }

    /**
     * Returns as many reviews as specified in the query by count by film id.
     * If no film is specified returns as many reviews as specified in the query by count.
     *
     * @param filmId the specified as identifier of film
     * @param count  is the amount of reviews returned
     * @return a List containing the reviews
     * @throws FilmNotFoundException if the film not found in the storage by ID
     * @see Review
     * @see ReviewDbStorageImpl
     */
    public List<Review> getAll(Long filmId, Integer count) {
        if (filmId == null) return reviewDbStorage.getAll(count).stream()
                .map(this::get)
                .collect(Collectors.toList());

        if (!filmDbStorage.containsIdFilm(filmId)) {
            throw new FilmNotFoundException(filmId + " id - film not found");
        }

        return reviewDbStorage.getAllByFilmId(filmId, count).stream()
                .map(this::get)
                .collect(Collectors.toList());
    }

    /**
     * User add like reaction on review
     *
     * @param id     the specified as identifier of review to add a like reaction on it
     * @param userId the specified as identifier of user, which added a like reaction
     * @return does like reaction add to review
     * @throws NoSuchIdException     if the review not found in the storage by ID
     * @throws UserNotFoundException if the user not found in the storage by ID
     * @see ReviewLikeDbStorageImpl
     * @see ReviewDbStorageImpl
     */
    public Boolean addLike(Long id, Long userId) {
        if (!reviewDbStorage.containsIdReview(id)) {
            throw new NoSuchIdException("?????????? ???? ID = " + id + " ???? ????????????");
        }
        if (!userDbStorage.containsIdUser(userId)) {
            throw new UserNotFoundException(userId + " id - user not found");
        }
        return reviewDbStorage.addReaction(id, userId, true);
    }

    /**
     * User add dislike reaction on review
     *
     * @param id     the specified as identifier of review to add a dislike reaction on it
     * @param userId the specified as identifier of user, which added a dislike reaction
     * @return does dislike reaction add to review
     * @throws NoSuchIdException     if the review not found in the storage by ID
     * @throws UserNotFoundException if the user not found in the storage by ID
     * @see ReviewDbStorageImpl
     */
    public Boolean addDislike(Long id, Long userId) {
        if (!reviewDbStorage.containsIdReview(id)) {
            throw new NoSuchIdException("?????????? ???? ID = " + id + " ???? ????????????");
        }
        if (!userDbStorage.containsIdUser(userId)) {
            throw new UserNotFoundException(userId + " id - user not found");
        }
        return reviewDbStorage.addReaction(id, userId, false);
    }

    /**
     * User delete reaction on review
     *
     * @param id       the specified as identifier of review to delete reaction on it
     * @param userId   the specified as identifier of user, which delete reaction
     * @param isUseful specified what type of reaction is going to be deleted
     * @return does reaction delete
     * @throws NoSuchIdException     if the review not found in the storage by ID
     * @throws UserNotFoundException if the user not found in the storage by ID
     * @see ReviewDbStorageImpl
     */
    public Boolean deleteReaction(Long id, Long userId, Boolean isUseful) {
        if (!reviewDbStorage.containsIdReview(id)) {
            throw new NoSuchIdException("?????????? ???? ID = " + id + " ???? ????????????");
        }
        if (!userDbStorage.containsIdUser(userId)) {
            throw new UserNotFoundException(userId + " id - user not found");
        }
        return reviewDbStorage.deleteReaction(id, userId, isUseful);
    }
}
