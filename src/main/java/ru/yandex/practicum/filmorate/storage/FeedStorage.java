package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.feedEnum.OperationType;

import java.util.List;

public interface FeedStorage {
    Feed addLike(Long user_id, OperationType operation, Long film_id);

    Feed addReview(Long user_id, OperationType operation, Long review_id);

    Feed addFriend(Long user_id, OperationType operation, Long friend_id);

    Feed add(Feed feed);

    List<Feed> get(Long user_id);
}
