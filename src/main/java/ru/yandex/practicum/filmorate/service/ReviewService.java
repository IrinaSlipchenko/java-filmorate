package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.NoSuchIdException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.db.FeedDbStorage;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.db.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;

import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.model.feedEnum.OperationType.*;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewDbStorage reviewDbStorage;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final FeedDbStorage feedDbStorage;


    public Review add(Review review){
        if( !userDbStorage.containsIdUser(review.getUserId()) ) {
            throw new UserNotFoundException(review.getUserId() + " id - user not found");
        }
        if( !filmDbStorage.containsIdFilm(review.getFilmId()) ) {
            throw new FilmNotFoundException(review.getFilmId() + " id - film not found");
        }
        Review resultReview = reviewDbStorage.add(review);
        feedDbStorage.addReview(review.getUserId(),ADD,review.getReviewId());
        return resultReview;
    }

    public Review update(Review review){
        if( review.getReviewId() == null ) return add(review);
        if( !reviewDbStorage.containsIdReview(review.getReviewId()) ) {
            throw new NoSuchIdException("Отзыв по ID = " + review.getReviewId() + " не найден");
        }
        review.setFilmId(reviewDbStorage.get(review.getReviewId()).getFilmId());
        review.setUserId(reviewDbStorage.get(review.getReviewId()).getUserId());
        Review resultReview =  reviewDbStorage.update(review);
        feedDbStorage.addReview(review.getUserId(),UPDATE,review.getReviewId());
        return resultReview;
    }

    public Review get( Long reviewId){
        if( !reviewDbStorage.containsIdReview(reviewId) ) {
            throw new NoSuchIdException("Отзыв по ID = " + reviewId + " не найден");
        }
        return reviewDbStorage.get(reviewId);
    }

    public Review delete (Long reviewId){
        if( !reviewDbStorage.containsIdReview(reviewId) ) {
            throw new NoSuchIdException("Отзыв по ID = " + reviewId + " не найден");
        }
        Review resultReview =  reviewDbStorage.delete(reviewId);
        feedDbStorage.addReview(resultReview.getUserId(),REMOVE,resultReview.getReviewId());
        return resultReview;
    }

    public List<Review> getAll(Long filmId, Integer count){
        if(filmId == null) return reviewDbStorage.getAll(count).stream()
                .map(this::get)
                .collect(Collectors.toList());

        filmDbStorage.findFilmById(filmId);

        return reviewDbStorage.getAllByFilmId(filmId,count).stream()
                .map(this::get)
                .collect(Collectors.toList());
    }

    public Boolean addLike(Long id, Long userId){
        if(!reviewDbStorage.containsIdReview(id)){
            throw new NoSuchIdException("Отзыв по ID = " + id + " не найден");
        }
        if( !userDbStorage.containsIdUser(userId) ) {
            throw new UserNotFoundException(userId + " id - user not found");
        }
        return reviewDbStorage.addReaction(id,userId,true);
    }

    public Boolean addDislike(Long id, Long userId){
        if(!reviewDbStorage.containsIdReview(id)){
            throw new NoSuchIdException("Отзыв по ID = " + id + " не найден");
        }
        if( !userDbStorage.containsIdUser(userId) ) {
            throw new UserNotFoundException(userId + " id - user not found");
        }
        return reviewDbStorage.addReaction(id,userId,false);
    }

    public Boolean deleteReaction(Long id, Long userId, Boolean isUseful){
        if(!reviewDbStorage.containsIdReview(id)){
            throw new NoSuchIdException("Отзыв по ID = " + id + " не найден");
        }
        if( !userDbStorage.containsIdUser(userId) ) {
            throw new UserNotFoundException(userId + " id - user not found");
        }
        return reviewDbStorage.deleteReaction(id ,userId, isUseful);
    }
}
