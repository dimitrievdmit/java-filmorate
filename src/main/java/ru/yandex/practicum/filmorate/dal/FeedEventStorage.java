package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.FeedEvent;

import java.util.List;

public interface FeedEventStorage {

    void addEvent(FeedEvent event);

    List<FeedEvent> getFeedForUser(Long userId, int limit);
}

