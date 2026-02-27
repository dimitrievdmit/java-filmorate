package ru.yandex.practicum.filmorate.dal;


import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmLikeRowMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Slf4j
@Repository
public class LikeDbStorage extends BaseDBRepository<Film> implements LikeStorage {

    private final FilmLikeRowMapper filmLikeRowMapper;

    private static final String SELECT_LIKES_BY_FILMS_QUERY = """
                SELECT
                    fl.film_id,
                    fl.user_id
                FROM film_likes fl
                WHERE fl.film_id IN (:filmIds)
            """;

    private static final String SELECT_LIKES_BY_USERS_QUERY = """
                SELECT
                    fl.film_id,
                    fl.user_id
                FROM film_likes fl
                WHERE fl.user_id IN (:userIds)
            """;

    private static final String SELECT_SINGLE_LIKE_QUERY = """
                SELECT
                    fl.film_id,
                    fl.user_id
                FROM film_likes fl
                WHERE fl.film_id = :filmId AND fl.user_id = :userId
            """;

    private static final String INSERT_LIKE_QUERY = """
                INSERT INTO film_likes (film_id, user_id)
                VALUES (:filmId, :userId)
            """;

    private static final String DELETE_LIKES_BY_FILMS_QUERY = "DELETE FROM film_likes WHERE film_id = :filmId";
    private static final String DELETE_SINGLE_LIKE_QUERY = """
                DELETE FROM film_likes
                WHERE film_id = :filmId AND user_id = :userId
            """;


    public LikeDbStorage(
            NamedParameterJdbcTemplate jdbc,
            RowMapper<Film> mapper,
            FilmLikeRowMapper filmLikeRowMapper
    ) {
        super(jdbc, mapper);
        this.filmLikeRowMapper = filmLikeRowMapper;
    }


    /**
     * Обновляет лайки фильма в БД.
     * При reset=true сначала удаляет все существующие лайки, затем добавляет новые.
     * При reset=false только добавляет новые (без удаления).
     */
    @Override
    public void updateFilmLikes(Film film, Boolean reset) {
        Long filmId = film.getId();
        if (reset) {
            update(DELETE_LIKES_BY_FILMS_QUERY, Map.of("filmId", filmId), false);
        }

        if (film.getLikes() != null && !film.getLikes().isEmpty()) {
            // Формируем массив параметров для каждого лайка
            SqlParameterSource[] batch = film.getLikes().stream()
                    .map(userId -> new MapSqlParameterSource()
                            .addValue("filmId", filmId)
                            .addValue("userId", userId))
                    .toArray(SqlParameterSource[]::new);

            // Выполняем batch-вставку
            jdbc.batchUpdate(INSERT_LIKE_QUERY, batch);
        }
    }

    @Override
    public Film filmAddLike(Film film, Long userId) {
        Map<String, Object> params = Map.of(
                "filmId", film.getId(),
                "userId", userId
        );
        update(INSERT_LIKE_QUERY, params, true);
        film.addLike(userId);
        return film;
    }

    @Override
    public Film filmRemoveLike(Film film, Long userId) {
        Map<String, Object> params = Map.of(
                "filmId", film.getId(),
                "userId", userId
        );
        update(DELETE_SINGLE_LIKE_QUERY, params, true);
        film.removeLike(userId);
        return film;
    }

    @Override
    public boolean checkIfExists(Film film, Long userId) {
        Map<String, Object> params = Map.of(
                "filmId", film.getId(),
                "userId", userId
        );
        try {
            Map.Entry<Long, Long> result = jdbc.queryForObject(SELECT_SINGLE_LIKE_QUERY, params, filmLikeRowMapper);
            return Optional.ofNullable(result).isPresent();
        } catch (EmptyResultDataAccessException ignored) {
            return false;
        }
    }

    @Override
    public Map<Long, Set<Long>> getUserLikesByFilms(List<Long> filmIds) {
        // Получаем все лайки (ид пользователей) для указанных фильмов
        // Запрос возвращает пары (film_id, user_id)
        return jdbc.query(
                        SELECT_LIKES_BY_FILMS_QUERY,
                        Map.of("filmIds", filmIds),
                        filmLikeRowMapper
                )
                .stream()
                // Группируем по film_id: для каждого фильма — набор ID пользователей, поставивших лайк
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toSet())
                ));
    }

    @Override
    public Map<Long, Set<Long>> getFilmLikesByUsers(List<Long> userIds) {
        // Получаем все лайки (ид фильмов) для указанных пользователей
        // Запрос возвращает пары (film_id, user_id)
        return jdbc.query(
                        SELECT_LIKES_BY_USERS_QUERY,
                        Map.of("userIds", userIds),
                        filmLikeRowMapper
                )
                .stream()
                // Группируем по user_id: для каждого пользователя — набор ID фильмов, которым поставили лайк
                .collect(Collectors.groupingBy(
                        Map.Entry::getValue,
                        Collectors.mapping(Map.Entry::getKey, Collectors.toSet())
                ));
    }

}
