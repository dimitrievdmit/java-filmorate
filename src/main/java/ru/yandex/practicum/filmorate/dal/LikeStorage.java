package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface LikeStorage {

    Map<Long, Set<Long>> getUserLikesByFilms(List<Long> filmIds);

    Map<Long, Set<Long>> getFilmLikesByUsers(List<Long> userIds);

    void updateFilmLikes(Film film, Boolean reset);

    Film filmAddLike(Film film, Long userId);

    Film filmRemoveLike(Film film, Long userId);

}
