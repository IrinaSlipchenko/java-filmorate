//package ru.yandex.practicum.filmorate.storage.db;
//
//import lombok.RequiredArgsConstructor;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.jdbc.core.JdbcTemplate;
//import ru.yandex.practicum.filmorate.model.*;
//import ru.yandex.practicum.filmorate.service.FilmService;
//import ru.yandex.practicum.filmorate.service.ReviewService;
//import ru.yandex.practicum.filmorate.service.UserService;
//
//import java.time.LocalDate;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import java.util.TreeSet;
//
//import static org.junit.jupiter.api.Assertions.assertAll;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static ru.yandex.practicum.filmorate.model.feedEnum.EventType.LIKE;
//import static ru.yandex.practicum.filmorate.model.feedEnum.OperationType.ADD;
//
//@SpringBootTest
//@AutoConfigureTestDatabase
//@RequiredArgsConstructor(onConstructor_ = @Autowired)
//class FeedDbStorageTest {
//    private final JdbcTemplate jdbcTemplate;
//    private final FilmService filmService;
//    private final UserService userService;
//    private final ReviewService reviewService;
//    private final FeedDbStorage feedDbStorage;
//    Film film;
//    User user;
//    Review review;
//
//    @BeforeEach
//    void configFilmUserAndReview() {
//        film = Film.builder()
//                .name("Film_Name1")
//                .description("Film_description")
//                .releaseDate(LocalDate.of(1990, 02, 05))
//                .duration(100)
//                .mpa(Mpa.builder().id(1).name("G").build())
//                .genres(new TreeSet<>(Collections.singleton(Genre.builder().id(1).name("Комедия").build())))
//                .build();
//        user = User.builder()
//                .email("name@email.com")
//                .login("login")
//                .name("name")
//                .birthday(LocalDate.of(1900, 10, 05))
//                .build();
//        review = Review.builder()
//                .content("New Review")
//                .isPositive(true)
//                .build();
//    }
//
//    @AfterEach
//    void clean() {
//        List<String> sql = Arrays.asList("DELETE FROM films"
//                , "DELETE FROM users"
//                , "DELETE FROM reviews"
//                , "ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1"
//                , "ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1"
//                , "ALTER TABLE reviews ALTER COLUMN review_id RESTART WITH 1");
//        sql.forEach(jdbcTemplate::update);
//    }
//
//    @Test
//    void ShouldAddLikeEvent() {
//        Long filmId = filmService.create(film).getId();
//        Long userId = userService.create(user).getId();
//        filmService.likeFilm(filmId, userId);
//        List<Feed> result = feedDbStorage.get(userId);
//        Feed feed = Feed.builder()
//                .entityId(result.get(0).getEntityId())
//                .timestamp(result.get(0).getTimestamp())
//                .userId(userId)
//                .eventId(filmId)
//                .eventType(LIKE)
//                .operation(ADD)
//                .build();
//        assertAll(
//                () -> assertEquals(1, result.size()),
//                () -> assertEquals(feed, result.get(0))
//        );
//    }
//}