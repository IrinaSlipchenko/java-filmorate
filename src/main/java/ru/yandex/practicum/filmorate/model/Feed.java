package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Feed {
    private LocalDateTime timestamp;
    private Long userId;
    private String eventType; // одно из значениий LIKE, REVIEW или FRIEND
    private String operation; // одно из значениий REMOVE, ADD, UPDATE
    private Long eventId;
    private Long entityId;
}
