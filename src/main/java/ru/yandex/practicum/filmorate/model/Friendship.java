package ru.yandex.practicum.filmorate.model;

import ru.yandex.practicum.filmorate.enums.FriendshipStatus;

public record Friendship(Long userId, Long friendId, FriendshipStatus status) {
}
