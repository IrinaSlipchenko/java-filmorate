package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findAll() throws Exception {
        mockMvc.perform(get("/users")).andExpect(status().isOk());
    }

    @Test
    void create() throws Exception {
        User user = User.builder()
                .name("Марина")
                .birthday(LocalDate.of(1985, 2, 1))
                .login("Mary_m")
                .email("marina@yandex.ru")
                .build();
        String json = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/users").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void update() throws Exception {
        User user = User.builder()
                .id(1)
                .name("Нина")
                .birthday(LocalDate.of(1990, 5, 9))
                .login("Nina")
                .email("nina@mail.ru")
                .build();
        String json = objectMapper.writeValueAsString(user);
        mockMvc.perform(put("/users").content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}