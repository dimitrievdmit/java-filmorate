package ru.yandex.practicum.filmorate.dto.event;

import ru.yandex.practicum.filmorate.enums.EventOperation;
import ru.yandex.practicum.filmorate.enums.EventType;

@SuppressWarnings("unused")
public record FeedEventDTO(
        Long timestamp,
        Long userId,
        EventType eventType,
        EventOperation operation,
        Long eventId,
        Long entityId
) {}
