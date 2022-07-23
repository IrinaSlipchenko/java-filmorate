package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.feedEnum.EventType;


@Data
@Builder
public class Feed {
    private Long timestamp;
    private Long userId;
    private String eventType; // одно из значениий LIKE, REVIEW или FRIEND
    private String operation; // одно из значениий REMOVE, ADD, UPDATE
    private Long eventId;
    private Long entityId;
}
