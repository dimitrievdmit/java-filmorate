package ru.yandex.practicum.filmorate.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

@RequiredArgsConstructor
public enum FriendshipStatus {
    CONFIRMED(1),
    UNCONFIRMED(2);

    @Getter
    private final int id;

    // Статический метод для поиска enum по id
    public static FriendshipStatus fromId(int id) {
        for (FriendshipStatus rating : values()) {
            if (rating.getId() == id) {
                return rating;
            }
        }
        throw new NotFoundException("Неизвестный FriendshipStatus id: " + id);
    }
}
