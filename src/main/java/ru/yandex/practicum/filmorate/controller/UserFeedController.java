package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.event.FeedEventDTO;
import ru.yandex.practicum.filmorate.mapper.FeedEventMapper;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/users")
@Slf4j
@Validated
@RequiredArgsConstructor
public class UserFeedController {

    private final FeedService feedService;
    private final UserService userService;

    @GetMapping("/{id}/feed")
    public Collection<FeedEventDTO> getUserFeed(
            @PathVariable Long id,
            @RequestParam(defaultValue = "20")
            @Positive(message = "Параметр limit должен быть положительным числом")
            int limit
    ) {
        userService.checkThatUserExists(id);
        List<FeedEvent> feed = feedService.getFeedForUser(id, limit);

        return feed.stream()
                .map(FeedEventMapper::toDTO)
                .collect(Collectors.toList());
    }

}
