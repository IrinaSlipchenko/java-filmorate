package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.controller.ReviewController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ReviewTest {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    private final ReviewController reviewController;
    private final JdbcTemplate jdbcTemplate;
    Film film;
    User user;
    Review review;
    private static Validator validator;
    Set<ConstraintViolation<Review>> violations;

    @BeforeAll
    static void initialization() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void configFilmUserAndReview() {
        film = Film.builder()
                .name("Film_Name")
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
        review = Review.builder()
                .content("New Review")
                .isPositive(true)
                .build();
    }

    @AfterEach
    void clean() {
        violations = null;
        List<String> sql = Arrays.asList("DELETE FROM films"
                , "DELETE FROM users"
                , "DELETE FROM reviews"
                , "ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1"
                , "ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1"
                , "ALTER TABLE reviews ALTER COLUMN review_id RESTART WITH 1");
        sql.forEach(jdbcTemplate::update);
    }

    @Test
    void shouldNotValidReviewIfContentIsBlank() {
        review.setFilmId(filmStorage.create(film).getId());
        review.setUserId(userStorage.create(user).getId());
        review.setContent("");
        violations = validator.validate(review);
        assertAll(
                () -> assertEquals(1, violations.size()),
                () -> assertEquals("Отсутсвует текст отзыва.", violations.iterator().next().getMessage())
        );
    }

    @Test
    void shouldNotValidateReviewWithNullType() {
        review.setFilmId(filmStorage.create(film).getId());
        review.setUserId(userStorage.create(user).getId());
        review.setIsPositive(null);
        violations = validator.validate(review);
        assertAll(
                () -> assertEquals(1, violations.size()),
                () -> assertEquals("Отсутствует тип отзыва : положительный или негативный.", violations.iterator().next().getMessage())
        );
    }

    @Test
    void shouldNotValidateReviewWithNullOrNotExistIdUser() {
        review.setFilmId(filmStorage.create(film).getId());
        review.setUserId(null);
        violations = validator.validate(review);
        assertAll(
                () -> assertEquals(1, violations.size()),
                () -> assertEquals("Отсутсвует ID пользователя, создавший отзыв."
                        , violations.iterator().next().getMessage())
        );
        review.setUserId(100l);
        assertThrows(UserNotFoundException.class, () -> reviewController.addReview(review));
    }

    @Test
    void shouldNotValidateReviewWithNullOrNotExistIdFilm() {
        review.setFilmId(null);
        review.setUserId(userStorage.create(user).getId());
        violations = validator.validate(review);
        assertAll(
                () -> assertEquals(1, violations.size()),
                () -> assertEquals("Отсутствует ID фильма, на который написан отзыв."
                        , violations.iterator().next().getMessage())
        );
        review.setFilmId(100l);
        assertThrows(FilmNotFoundException.class, () -> reviewController.addReview(review));
    }

}