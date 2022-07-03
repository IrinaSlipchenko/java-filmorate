package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
@Builder
public class User {
    private long id;
    @Email
    private String email;
    @NotBlank
    @Pattern(regexp = "\\S*$")
    private String login;
    private String name;
    @Past
    private LocalDate birthday;
}
