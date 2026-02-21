package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FeedEventStorage;
import ru.yandex.practicum.filmorate.enums.EventOperation;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.model.FeedEvent;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedEventStorage feedEventStorage;

    public List<FeedEvent> getFeedForUser(Long userId, int limit) {
        return feedEventStorage.getFeedForUser(userId, limit);
    }

    public void logEvent(Long userId, EventType eventType, EventOperation operation, Long entityId) {
        FeedEvent event = new FeedEvent();
        event.setUserId(userId);
        event.setEventType(eventType);
        event.setOperation(operation);
        event.setEntityId(entityId);
        event.setTimestamp(Instant.now().toEpochMilli());
        feedEventStorage.addEvent(event);
    }
}
