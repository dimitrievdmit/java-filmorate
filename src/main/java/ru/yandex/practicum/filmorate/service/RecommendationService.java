package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.LikeStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Service
@Slf4j
public class RecommendationService {
    private final LikeStorage likeStorage;
    private final FilmService filmService;

    private record UserWithSimilarity(Long userId, int similarity) {
    }

    public RecommendationService(
            LikeStorage likeStorage,
            FilmService filmService
    ) {
        this.likeStorage = likeStorage;
        this.filmService = filmService;
    }

    public List<Film> getRecommendedFilms(Long userId, Long count) {
        log.info("Получение {} рекомендованных фильмов пользователю {}", count, userId);

        if (count <= 0) {
            return Collections.emptyList();
        }
        // получить лайки (ид фильмов) целевого пользователя
        Set<Long> targetUserLikes = getUserLikes(userId);

        if (targetUserLikes.isEmpty()) {
            return Collections.emptyList();
        }

        // Найти похожих пользователей
        List<UserWithSimilarity> similarUsers = findSimilarUsers(userId, targetUserLikes);

        if (similarUsers.isEmpty()) {
            return Collections.emptyList();
        }

        // Получить все фильмы от похожих пользователей
        // Исключить фильмы, которые пользователь уже лайкнул
        // Отсортировать по частоте упоминания среди похожих пользователей
        Map<Long, Long> filmFrequency = getFilmsWithFrequency(targetUserLikes, similarUsers);

        // Преобразовать ID в объекты Film и вернуть топ‑count
        return getTopFilmsByFrequency(filmFrequency, count);
    }

    private Set<Long> getUserLikes(Long userId) {
        Map<Long, Set<Long>> userLikesMap = likeStorage.getFilmLikesByUsers(List.of(userId));
        return userLikesMap.getOrDefault(userId, Collections.emptySet());
    }


    private List<UserWithSimilarity> findSimilarUsers(Long targetUserId, Set<Long> targetUserLikes) {
        // Шаг 1: получить лайки (ид пользователей) всех фильмов целевого пользователя
        Map<Long, Set<Long>> filmLikesMap = likeStorage.getUserLikesByFilms(new ArrayList<>(targetUserLikes));

        // Шаг 2: собрать всех пользователей, которые лайкали эти фильмы
        Set<Long> candidateUserIds = new HashSet<>();
        for (Set<Long> usersWhoLikedFilm : filmLikesMap.values()) {
            candidateUserIds.addAll(usersWhoLikedFilm);
        }
        candidateUserIds.remove(targetUserId); // исключить целевого пользователя

        if (candidateUserIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Шаг 3: получить лайки кандидатов
        Map<Long, Set<Long>> candidateLikesMap = likeStorage.getFilmLikesByUsers(new ArrayList<>(candidateUserIds));

        // Шаг 4: рассчитать схожесть для каждого кандидата
        List<UserWithSimilarity> similarUsers = new ArrayList<>();
        for (Long candidateId : candidateUserIds) {
            Set<Long> candidateLikes = candidateLikesMap.getOrDefault(candidateId, Collections.emptySet());
            int similarity = calculateIntersectionSize(targetUserLikes, candidateLikes);
            if (similarity >= 1) { // минимум 1 общий лайк
                similarUsers.add(new UserWithSimilarity(candidateId, similarity));
            }
        }

        // Отсортировать по убыванию схожести
        similarUsers.sort(Comparator.comparing(UserWithSimilarity::similarity).reversed());
        return similarUsers;
    }


    private Map<Long, Long> getFilmsWithFrequency(Set<Long> targetUserLikes, List<UserWithSimilarity> similarUsers) {

        // Получить все фильмы от похожих пользователей
        List<Long> userIds = similarUsers.stream()
                .map(UserWithSimilarity::userId)
                .collect(Collectors.toList());

        Map<Long, Set<Long>> userLikesMap = likeStorage.getFilmLikesByUsers(userIds);

        Set<Long> allFilmsFromSimilar = new HashSet<>();
        for (Set<Long> films : userLikesMap.values()) {
            allFilmsFromSimilar.addAll(films);
        }

        // Исключить фильмы, которые пользователь уже лайкнул
        allFilmsFromSimilar.removeAll(targetUserLikes);

        if (allFilmsFromSimilar.isEmpty()) {
            return Collections.emptyMap();
        }

        // Отсортировать по частоте упоминания среди похожих пользователей
        Map<Long, Long> frequencyMap = new HashMap<>();
        for (Long filmId : allFilmsFromSimilar) {
            long count = userLikesMap.values().stream()
                    .filter(likes -> likes.contains(filmId))
                    .count();
            frequencyMap.put(filmId, count);
        }

        return frequencyMap;
    }


    private int calculateIntersectionSize(Set<Long> set1, Set<Long> set2) {
        Set<Long> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        return intersection.size();
    }

    private List<Film> getTopFilmsByFrequency(Map<Long, Long> filmFrequency, Long count) {
        List<Long> topFilmIds = filmFrequency.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(count)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (topFilmIds.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            Collection<Film> films = filmService.getFilms(topFilmIds);
            Map<Long, Film> filmMap = films.stream()
                    .collect(Collectors.toMap(Film::getId, Function.identity()));
            return topFilmIds.stream()
                    .map(filmMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (NotFoundException e) {
            log.warn("Не найдены фильмы для рекомендаций: {}", topFilmIds);
            return Collections.emptyList();
        }
    }

}
