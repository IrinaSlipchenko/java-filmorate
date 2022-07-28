package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.Set;

/**
 * This class describes the user.
 * User parameters should meet some basic requirements
 */
@Data
@Builder
public class User {

    /**
     * the identifier for the user.
     * Mostly used to specify the object for the storage interactions.
     */
    private Long id;

    /**
     * the e-mail address of user, should meet requirements for e-mail markup
     */
    @Email
    private String email;

    /**
     * login of the user, must be specified and have no spaces in it
     */
    @NotBlank
    @Pattern(regexp = "\\S*$")
    private String login;

    /**
     * the name of user
     */
    private String name;

    /**
     * the birthday of the user, must be in the past as user data created or updated
     */
    @Past
    private LocalDate birthday;

    /**
     * the identifiers of users added by user as his friends
     */
    private Set<Long> friends;
}
