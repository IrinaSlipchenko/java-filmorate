package ru.yandex.practicum.filmorate.model.feedEnum;

import ru.yandex.practicum.filmorate.model.Feed;

/**
 * This class helps to define the event type to be recorded for user activity in feed.
 *
 * @see Feed
 */
public enum EventType {

    /**
     * event: user activity on likes for film
     */
    LIKE,

    /**
     * event: user activity on review for film
     */
    REVIEW,

    /**
     * event: user activity on friends
     */
    FRIEND
}
