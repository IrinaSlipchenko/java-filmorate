package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.feedEnum.EventType;
import ru.yandex.practicum.filmorate.model.feedEnum.OperationType;

/**
 * This class describes the feed element of user activities as event.
 */
@Data
@Builder
public class Feed {

    /**
     * the time of user activity performed
     */
    private Long timestamp;

    /**
     * the identifier of user whose activity is recorded
     *
     * @see User
     */
    private Long userId;

    /**
     * the type of event for user activity,
     * could be one of the following: LIKE, REVIEW, FRIEND
     *
     * @see EventType
     */
    private EventType eventType;

    /**
     * the type of action made to storage data for user activity,
     * could be one of the following: REMOVE, ADD, UPDATE
     *
     * @see OperationType
     */
    private OperationType operation;

    /**
     * the identifier for the user activity (event)
     * Mostly used to specify the object for the storage interactions.
     */
    private Long eventId;

    /**
     * the identifier for the entity of user activity (event)
     * the entity could be as follows: adding/removing friends, adding/removing likes, adding/removing reviews
     * Mostly used to specify the object for the storage interactions.
     *
     * @see User#getFriends()
     * @see Review
     */
    private Long entityId;
}
