package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;
<<<<<<< HEAD
import java.util.SortedSet;
=======
import java.util.TreeSet;
>>>>>>> 3592060 (directors without search)

@Data
@Builder
public class Film {
    private Long id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    private LocalDate releaseDate;
    @Positive
    private Integer duration;
    private Set<Long> likes;
<<<<<<< HEAD
    private SortedSet<Genre> genres;
    @NotNull
=======
    private TreeSet<Genre> genres;
>>>>>>> 3592060 (directors without search)
    private Mpa mpa;
    private Set<Director> directors;
}
