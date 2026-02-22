package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.enums.EventOperation;
import ru.yandex.practicum.filmorate.enums.EventType;

@Data
public class FeedEvent {
    private Long id;
    private Long userId;
    private EventType eventType;
    private EventOperation operation;
    private Long entityId;
    private Long timestamp;
}
