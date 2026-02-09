package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.enums.FriendshipStatus;

@Data
public class Friendship {
    private final Long userId;
    private final Long friendId;
    private final FriendshipStatus status;
}
