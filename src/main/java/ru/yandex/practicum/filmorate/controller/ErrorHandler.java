package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

/**
 * The class helps to handle program exceptions and send info to user as error response with specified data and status
 */
@RestControllerAdvice
public class ErrorHandler {

    /**
     * @param e thrown UserNotFoundException
     * @return response status and data as ErrorResponse json with specified error
     * @see UserNotFoundException
     * @see ErrorResponse
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(final UserNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * @param e thrown FilmNotFoundException
     * @return response status and data as ErrorResponse json with specified error
     * @see FilmNotFoundException
     * @see ErrorResponse
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleFilmNotFoundException(final FilmNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * @param e thrown DirectorNotFoundException
     * @return response status and data as ErrorResponse json with specified error
     * @see DirectorNotFoundException
     * @see ErrorResponse
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleDirectorNotFoundException(final DirectorNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * @param e thrown ValidationException
     * @return response status and data as ErrorResponse json with specified error
     * @see ValidationException
     * @see ErrorResponse
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * @param e thrown MethodArgumentNotValidException
     * @return response status and data as ErrorResponse json with specified error
     * @see MethodArgumentNotValidException
     * @see ErrorResponse
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        return new ErrorResponse(e.getFieldError().getDefaultMessage());
    }

    /**
     * @param e thrown NoSuchIdException
     * @return response status and data as ErrorResponse json with specified error
     * @see NoSuchIdException
     * @see ErrorResponse
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoSuchIdException(final NoSuchIdException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * @param e thrown NoSuchReactFind
     * @return response status and data as ErrorResponse json with specified error
     * @see NoSuchReactFind
     * @see ErrorResponse
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoSuchReactFind(final NoSuchReactFind e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * @param e thrown AlreadyExistException
     * @return response status and data as ErrorResponse json with specified error
     * @see AlreadyExistException
     * @see ErrorResponse
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleAlreadyExistException(final AlreadyExistException e) {
        return new ErrorResponse(e.getMessage());
    }

    /**
     * @param e thrown GenreNotFoundException
     * @return response status and data as ErrorResponse json with specified error
     * @see GenreNotFoundException
     * @see ErrorResponse
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleGenreNotFoundException(final GenreNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }
}