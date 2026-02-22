package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.event.FeedEventDTO;
import ru.yandex.practicum.filmorate.model.FeedEvent;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeedEventMapper {
    public static FeedEventDTO toDTO(FeedEvent event) {
        if (event == null) return null;
        FeedEventDTO dto = new FeedEventDTO();
        dto.setTimestamp(event.getTimestamp());
        dto.setUserId(event.getUserId());
        dto.setEventType(event.getEventType());
        dto.setOperation(event.getOperation());
        dto.setEventId(event.getId() != null ? event.getId() : 0L);
        dto.setEntityId(event.getEntityId());
        return dto;
    }
}
