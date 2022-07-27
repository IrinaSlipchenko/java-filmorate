package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

/**
 * The class helps listen to user request at "/reviews"
 */
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    /**
     * @see ReviewService
     */
    private final ReviewService reviewService;

    /**
     * @param review the json specified as review object with no identifier received to save in storage
     * @return review specified with identifier and saved with given parameters in storage
     * @see Review
     */
    @PostMapping
    public Review addReview(@Valid @RequestBody Review review) {
        return reviewService.add(review);
    }

    /**
     * @param review the json specified as review object with existing identifier received to save in storage
     * @return review specified with identifier and saved with updated parameters in storage
     * @see Review
     */
    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        return reviewService.update(review);
    }

    /**
     * @param id the specified identifier for the review to be deleted.
     * @return review specified with identifier and deleted from storage
     * @see Review
     */
    @DeleteMapping("/{id}")
    public Review deleteReview(@PathVariable Long id) {
        return reviewService.delete(id);
    }

    /**
     * @param id the specified identifier for the review to be searched
     * @return the review saved in storage under specified identifier
     * @see Review
     */
    @GetMapping("/{id}")
    public Review getReview(@PathVariable Long id) {
        return reviewService.get(id);
    }

    /**
     * @param filmId the specified identifier for the film, which reviews to be searched
     * @param count  the number of reviews to be returned, if absent then count = 10
     * @return reviews of the certain film under this film specified identifier
     * @see Review
     * @see Film
     */
    @GetMapping
    public List<Review> getAllReviews(@RequestParam(required = false) Long filmId,
                                      @RequestParam(defaultValue = "10") Integer count) {
        return reviewService.getAll(filmId, count);
    }

    /**
     * @param id     the specified identifier for the review to be liked
     * @param userId the identifier for the user who liked review
     * @return true or false in case if review specified with identifier saved with updated likes in storage
     * @see Review
     * @see User
     */
    @PutMapping("/{id}/like/{userId}")
    public Boolean addLike(@PathVariable Long id,
                           @PathVariable Long userId) {
        return reviewService.addLike(id, userId);
    }

    /**
     * @param id     the specified identifier for the review to be disliked
     * @param userId the identifier for the user who disliked review
     * @return true or false in case if review specified with identifier saved with updated dislikes in storage
     * @see Review
     * @see User
     */
    @PutMapping("/{id}/dislike/{userId}")
    public Boolean addDislike(@PathVariable Long id,
                              @PathVariable Long userId) {
        return reviewService.addDislike(id, userId);
    }

    /**
     * @param id     the identifier for the review, like for which to be deleted from storage
     * @param userId the identifier for the user who liked film
     * @return true or false in case if review specified with identifier saved with updated likes in storage
     * @see Review
     * @see User
     */
    @DeleteMapping("/{id}/like/{userId}")
    public Boolean deleteLike(@PathVariable Long id,
                              @PathVariable Long userId) {
        return reviewService.deleteReaction(id, userId, true);
    }

    /**
     * @param id     the identifier for the review, dislike for which to be deleted from storage
     * @param userId the identifier for the user who disliked film
     * @return true or false in case if review specified with identifier saved with updated dislikes in storage
     * @see Review
     * @see User
     */
    @DeleteMapping("/{id}/dislike/{userId}")
    public Boolean deleteDislike(@PathVariable Long id,
                                 @PathVariable Long userId) {
        return reviewService.deleteReaction(id, userId, false);
    }

}