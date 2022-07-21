package ru.yandex.practicum.filmorate.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

//Добавление нового отзыва.
    @PostMapping
    public Review addReview( @Valid @RequestBody Review review ) {
        return reviewService.add(review);
    }

//Редактирование уже имеющегося отзыва.
    @PutMapping
    public Review updateReview( @Valid @RequestBody Review review ) {
        return reviewService.update(review);
    }

//Удаление уже имеющегося отзыва.
    @DeleteMapping("/{id}")
    public Review deleteReview(@PathVariable Long id ) {
        return reviewService.delete(id);
    }

//Получение отзыва по идентификатору.
    @GetMapping("/{id}")
    public Review getReview( @PathVariable Long id ) {
        return reviewService.get(id);
    }

//Получение всех отзывов по идентификатору фильма, если фильм не указа но все. Если кол-во не указано то 10.
    @GetMapping
    public List<Review> getAllReviews(@RequestParam(required = false) Long filmId,
                                      @RequestParam(defaultValue = "10") Integer count){
        return reviewService.getAll( filmId, count );
    }

//  пользователь ставит лайк отзыву.
    @PutMapping("/{id}/like/{userId}")
    public Boolean addLike(@PathVariable Long id,
                   @PathVariable Long userId) {
        return reviewService.addLike( id, userId );
    }

//  пользователь ставит дизлайк отзыву.
    @PutMapping("/{id}/dislike/{userId}")
    public Boolean addDislike(@PathVariable Long id,
                           @PathVariable Long userId) {
        return reviewService.addDislike( id, userId );
    }

// пользователь удаляет лайк/дизлайк отзыву.
    @DeleteMapping("/{id}/like/{userId}")
    public Boolean deleteLike(@PathVariable Long id,
                              @PathVariable Long userId) {
        return reviewService.deleteLike(id, userId);
    }

//-  пользователь удаляет дизлайк отзыву.
    @DeleteMapping ("/{id}/dislike/{userId}")
    public Boolean deleteDislike(@PathVariable Long id,
                            @PathVariable Long userId) {
        return reviewService.deleteDislike(id, userId);
    }

}