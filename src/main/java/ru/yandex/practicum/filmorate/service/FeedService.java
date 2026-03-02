package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FeedEventStorage;
import ru.yandex.practicum.filmorate.enums.EventOperation;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.validator.UserExistenceValidator;

import java.time.Instant;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeedService {

    private final FeedEventStorage feedEventStorage;
    private final UserExistenceValidator userExistenceValidator;

    public List<FeedEvent> getFeedForUser(Long userId, int limit) {
        userExistenceValidator.checkThatUserExists(userId);
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
        log.info("Событие добавлено в ленту: userId={}, eventType={}, operation={}, entityId={}", userId, eventType, operation, entityId);
    }
}
