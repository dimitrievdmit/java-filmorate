package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.event.FeedEventDTO;
import ru.yandex.practicum.filmorate.model.FeedEvent;

@SuppressWarnings("unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeedEventMapper {
    public static FeedEventDTO toDTO(FeedEvent event) {
        if (event == null) return null;
        return new FeedEventDTO(
            event.getTimestamp(),
            event.getUserId(),
            event.getEventType(),
            event.getOperation(),
            event.getId() != null ? event.getId() : 0L,
            event.getEntityId()
        );
    }
}
