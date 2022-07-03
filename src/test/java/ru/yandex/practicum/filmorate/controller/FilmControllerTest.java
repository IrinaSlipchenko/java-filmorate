package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findAll() throws Exception {
        mockMvc.perform(get("/films")).andExpect(status().isOk());
    }

    @Test
    void create() throws Exception {
        Film film = Film.builder()
                .name("Тест фильм")
                .description("Тест описание")
                .releaseDate(LocalDate.of(2000, 10, 17))
                .duration(120)
                .build();
        String json = objectMapper.writeValueAsString(film);
        mockMvc.perform(post("/films").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void update() throws Exception {
        Film film = Film.builder()
                .id(1)
                .name("Прогулка")
                .description("Канатаходец задумал пройти между башнями-близнецами")
                .releaseDate(LocalDate.of(2009, 11, 15))
                .duration(100)
                .build();
        String json = objectMapper.writeValueAsString(film);
        mockMvc.perform(put("/films").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @MethodSource("invalidFilmSourse")
    @ParameterizedTest(name = "{0}")
    void test_invalidFilmShouldBadReq(String name, Film film) throws Exception {
        String json = objectMapper.writeValueAsString(film);
        mockMvc.perform(post("/films").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    private static Stream<Arguments> invalidFilmSourse() {
        return Stream.of(
                Arguments.of(
                        "Empty name",
                        Film.builder()
                                .name("")
                                .description("Тест описание")
                                .releaseDate(LocalDate.of(2000, 10, 17))
                                .duration(120)
                                .build()

                ),
                Arguments.of(
                        "Wrong date",
                        Film.builder()
                                .name("The Bourne Identity")
                                .description("Мировая премьера картины состоялась в Лос-Анджелесе.")
                                .releaseDate(LocalDate.of(1800, 10, 17))
                                .duration(120)
                                .build()

                )
        );
    }
}