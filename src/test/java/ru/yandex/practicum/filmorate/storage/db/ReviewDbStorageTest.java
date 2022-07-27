package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NoSuchReactFind;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ReviewDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    private final ReviewStorage reviewStorage;
    Film film;
    User user;
    User user2;
    Review review;
    Review review2;

    @BeforeEach
    void configFilmUserAndReview() {
        film = Film.builder()
                .name("Film_Name1")
                .description("Film_description")
                .releaseDate(LocalDate.of(1990, 02, 05))
                .duration(100)
                .mpa(Mpa.builder().id(1).name("G").build())
                .genres(new TreeSet<>(Collections.singleton(Genre.builder().id(1).name("Комедия").build())))
                .build();
        user = User.builder()
                .email("name@email.com")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(1900, 10, 05))
                .build();
        user2 = User.builder()
                .email("name2@email.com")
                .login("login2")
                .name("name2")
                .birthday(LocalDate.of(1900, 10, 02))
                .build();
        review = Review.builder()
                .content("New Review")
                .isPositive(true)
                .build();
        review2 = Review.builder()
                .content("New Review2")
                .isPositive(false)
                .build();
    }

    @AfterEach
    void clean() {
        List<String> sql = Arrays.asList("DELETE FROM films"
                , "DELETE FROM users"
                , "DELETE FROM reviews"
                , "ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1"
                , "ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1"
                , "ALTER TABLE reviews ALTER COLUMN review_id RESTART WITH 1");
        sql.forEach(jdbcTemplate::update);
    }

    @Test
    void shouldAddUpdateAndDeleteReview() {
        review.setFilmId(filmStorage.create(film).getId());
        review.setUserId(userStorage.create(user).getId());
        review.setReviewId(reviewStorage.add(review).getReviewId());
        assertAll(
                () -> assertEquals(true, reviewStorage.containsIdReview(review.getReviewId())),
                () -> assertEquals(review, reviewStorage.get(review.getReviewId())),
                () -> assertEquals(1, reviewStorage.getAll(10).size())
        );
        review.setContent("Review_Updated");
        reviewStorage.update(review);
        assertAll(
                () -> assertEquals(review, reviewStorage.get(review.getReviewId())),
                () -> assertEquals(1, reviewStorage.getAll(10).size())
        );
        reviewStorage.delete(review.getReviewId());
        assertAll(
                () -> assertEquals(false, reviewStorage.containsIdReview(review.getReviewId())),
                () -> assertEquals(0, reviewStorage.getAll(10).size())
        );
    }

    @Test
    void shouldAddReturnTwoReviews() {
        review.setFilmId(filmStorage.create(film).getId());
        review.setUserId(userStorage.create(user).getId());
        review.setReviewId(reviewStorage.add(review).getReviewId());
        review2.setFilmId(review.getFilmId());
        review2.setUserId(userStorage.create(user2).getId());
        review2.setReviewId(reviewStorage.add(review2).getReviewId());
        assertAll(
                () -> assertEquals(review, reviewStorage.get(review.getReviewId())),
                () -> assertEquals(review2, reviewStorage.get(review2.getReviewId())),
                () -> assertEquals(2, reviewStorage.getAll(10).size()),
                () -> assertEquals(2, reviewStorage.getAllByFilmId(review.getFilmId(), 10).size())
        );
    }


    @Test
    void shouldAddUpdateAndDeleteReactionAndReturnSortedReviews() {
        Long userId = userStorage.create(user).getId();
        Long userId2 = userStorage.create(user2).getId();
        review.setFilmId(filmStorage.create(film).getId());
        review.setUserId(userId);
        review.setReviewId(reviewStorage.add(review).getReviewId());
        review2.setFilmId(review.getFilmId());
        review2.setUserId(userId2);
        review2.setReviewId(reviewStorage.add(review2).getReviewId());
        reviewStorage.addReaction(review.getReviewId(), userId2, true);
        assertAll(
                () -> assertEquals(review.getReviewId()
                        , reviewStorage.getAllByFilmId(review.getFilmId(), 10).get(0)),
                () -> assertEquals(review2.getReviewId()
                        , reviewStorage.getAllByFilmId(review.getFilmId(), 10).get(1))
        );
        reviewStorage.addReaction(review.getReviewId(), userId2, false);
        assertAll(
                () -> assertEquals(review2.getReviewId()
                        , reviewStorage.getAllByFilmId(review.getFilmId(), 10).get(0)),
                () -> assertEquals(review.getReviewId()
                        , reviewStorage.getAllByFilmId(review.getFilmId(), 10).get(1))
        );
        reviewStorage.addReaction(review2.getReviewId(), userId2, true);
        assertAll(
                () -> assertEquals(1
                        , reviewStorage.get(review2.getReviewId()).getUseful()),
                () -> assertEquals(-1
                        , reviewStorage.get(review.getReviewId()).getUseful())
        );
        reviewStorage.deleteReaction(review2.getReviewId(), userId2, true);
        assertEquals(0, reviewStorage.get(review2.getReviewId()).getUseful());

    }

    @Test
    void shouldThrowExceptionIfUserAlreadyHaveReviewForTheFilm() {
        review.setFilmId(filmStorage.create(film).getId());
        review.setUserId(userStorage.create(user).getId());
        review.setReviewId(reviewStorage.add(review).getReviewId());
        assertThrows(AlreadyExistException.class, () -> reviewStorage.add(review));
    }

    @Test
    void shouldThrowExceptionIfReactionNotFind() {
        Long userId = userStorage.create(user).getId();
        review.setFilmId(filmStorage.create(film).getId());
        review.setUserId(userId);
        Long reviewId = reviewStorage.add(review).getReviewId();
        review.setReviewId(reviewId);
        assertThrows(NoSuchReactFind.class, () -> reviewStorage.deleteReaction(reviewId, userId, true));
        reviewStorage.addReaction(reviewId, userId, true);
        assertThrows(NoSuchReactFind.class, () -> reviewStorage.deleteReaction(reviewId, userId, false));
        reviewStorage.addReaction(reviewId, userId, false);
        assertThrows(NoSuchReactFind.class, () -> reviewStorage.deleteReaction(reviewId, userId, true));
    }

}