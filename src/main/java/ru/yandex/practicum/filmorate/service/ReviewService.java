package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NoSuchIdException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.db.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    private final ReviewDbStorage reviewDbStorage;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final FeedDbStorage feedDbStorage;


    @Autowired
    public ReviewService(ReviewDbStorage reviewDbStorage
            , UserDbStorage userDbStorage
            , FilmDbStorage filmDbStorage
            , FeedDbStorage feedDbStorage) {
        this.reviewDbStorage = reviewDbStorage;
        this.userDbStorage = userDbStorage;
        this.filmDbStorage = filmDbStorage;
        this.feedDbStorage = feedDbStorage;
    }

    public Review add(Review review){
        userDbStorage.findUserById(review.getUserId());
        filmDbStorage.findFilmById(review.getFilmId());
        return reviewDbStorage.add(review);
    }

    public Review update(Review review){
        if( review.getReviewId() == null ) return add(review);
        if( !reviewDbStorage.containsIdReview(review.getReviewId()) ) {
            throw new NoSuchIdException("Отзыв по ID = " + review.getReviewId() + " не найден");
        }
        review.setFilmId(reviewDbStorage.get(review.getReviewId()).getFilmId());
        review.setUserId(reviewDbStorage.get(review.getReviewId()).getUserId());
        return reviewDbStorage.update(review);
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
        return reviewDbStorage.delete(reviewId);
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
            throw  new NoSuchIdException("Отзыв по ID = " + id + " не найден");
        }
        userDbStorage.findUserById(userId);
        return reviewDbStorage.addReaction(id,userId,true);
    }

    public Boolean addDislike(Long id, Long userId){
        if(!reviewDbStorage.containsIdReview(id)){
            throw  new NoSuchIdException("Отзыв по ID = " + id + " не найден");
        }
        userDbStorage.findUserById(userId);
        return reviewDbStorage.addReaction(id,userId,false);
    }

    public Boolean deleteDislike(Long id, Long userId){
        if(!reviewDbStorage.containsIdReview(id)){
            throw  new NoSuchIdException("Отзыв по ID = " + id + " не найден");
        }
        userDbStorage.findUserById(userId);
        return reviewDbStorage.deleteDislike(id,userId);
    }
    public Boolean deleteLike(Long id, Long userId){
        if(!reviewDbStorage.containsIdReview(id)){
            throw  new NoSuchIdException("Отзыв по ID = " + id + " не найден");
        }
        userDbStorage.findUserById(userId);
        return reviewDbStorage.deleteLike(id,userId);
    }



}
