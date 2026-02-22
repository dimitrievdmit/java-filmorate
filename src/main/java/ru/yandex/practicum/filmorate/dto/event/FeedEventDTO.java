package ru.yandex.practicum.filmorate.dto.event;

import lombok.Data;
import ru.yandex.practicum.filmorate.enums.EventOperation;
import ru.yandex.practicum.filmorate.enums.EventType;

@Data
public class FeedEventDTO {
    private Long timestamp;
    private Long userId;
    private EventType eventType;
    private EventOperation operation;
    private Long eventId;
    private Long entityId;
}
