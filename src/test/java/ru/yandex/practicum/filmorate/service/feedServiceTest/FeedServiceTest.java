package ru.yandex.practicum.filmorate.service.feedServiceTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.enums.EventOperation;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.filmorate.mock.MockUsers.getValidUser;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FeedServiceTest {

    private final FeedService feedService;
    private final UserService userService;

    @Test
    void shouldReturnEmptyListForUserWithNoEvents() {
        Long userId = userService.createUser(getValidUser()).getId();
        List<FeedEvent> feed = feedService.getFeedForUser(userId, 10);
        assertTrue(feed.isEmpty(), "Ожидался пустой список, так как у пользователя нет событий");
    }

    @Test
    void shouldLogEventAndRetrieveInOrder() {
        Long userId = userService.createUser(getValidUser()).getId();
        feedService.logEvent(userId, EventType.LIKE, EventOperation.ADD, 101L);

        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        feedService.logEvent(userId, EventType.FRIEND, EventOperation.REMOVE, 202L);

        List<FeedEvent> feed = feedService.getFeedForUser(userId, 10);
        assertEquals(2, feed.size(), "Должно вернуться ровно 2 события");

        assertEquals(101L, feed.getFirst().getEntityId());
        assertEquals(EventType.LIKE, feed.get(0).getEventType());
        assertEquals(EventOperation.ADD, feed.get(0).getOperation());

        assertEquals(202L, feed.get(1).getEntityId());
        assertEquals(EventType.FRIEND, feed.get(1).getEventType());
        assertEquals(EventOperation.REMOVE, feed.get(1).getOperation());

        assertNotNull(feed.get(0).getTimestamp());
        assertNotNull(feed.get(1).getTimestamp());
    }

    @Test
    void shouldLimitFeedResults() {
        Long userId = userService.createUser(getValidUser()).getId();

        for (int i = 0; i < 5; i++) {
            feedService.logEvent(userId, EventType.LIKE, EventOperation.ADD, (long) (100 + i));
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        List<FeedEvent> feed = feedService.getFeedForUser(userId, 2);

        assertEquals(2, feed.size(), "Должно вернуть указанное значение лимита (2)");
        assertEquals(100L, feed.get(0).getEntityId());
        assertEquals(101L, feed.get(1).getEntityId());
    }
}