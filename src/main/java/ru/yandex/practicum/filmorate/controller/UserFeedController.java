package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.event.FeedEventDTO;
import ru.yandex.practicum.filmorate.mapper.FeedEventMapper;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.service.FeedService;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserFeedController {

    private final FeedService feedService;

    @GetMapping("/{id}/feed")
    public Collection<FeedEventDTO> getUserFeed(@PathVariable Long id,
                                                @RequestParam(defaultValue = "10") int limit) {
        List<FeedEvent> feed =
                (List<FeedEvent>) feedService.getFeedForUser(id, limit);

        return feed.stream()
                .map(FeedEventMapper::toDTO)
                .collect(Collectors.toList());
    }

}
