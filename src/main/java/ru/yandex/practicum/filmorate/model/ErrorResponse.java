package ru.yandex.practicum.filmorate.model;

/**
 * The class helps to form response body for user for exceptions cases
 */
public class ErrorResponse {

    /**
     * the error message of thrown exception or any put in here to form response body for user
     */
    private final String error;

    /**
     * @param error the information to be stored in response body for user notification
     */
    public ErrorResponse(String error) {
        this.error = error;
    }

    /**
     * @return the error message of caught exception or any other message for user inform
     */
    public String getError() {
        return error;
    }
}
