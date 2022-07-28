package ru.yandex.practicum.filmorate.model.feedEnum;

import ru.yandex.practicum.filmorate.model.Feed;

/**
 * This class helps to define the operation type to be recorded for user activity in feed.
 *
 * @see Feed
 */
public enum OperationType {

    /**
     * operation: removing user's like for film / review for film / friend from friendship
     */
    REMOVE,

    /**
     * operation: adding user's like for film / review for film / friend to friendship
     */
    ADD,

    /**
     * operation: updating user's like for film / review for film / friend to friendship
     */
    UPDATE
}
