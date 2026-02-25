package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class BaseDBRepository<T> {
    protected final NamedParameterJdbcTemplate jdbc;
    protected final RowMapper<T> mapper;

    /**
     * Выполнить запрос, возвращающий один объект (или null).
     */
    protected Optional<T> findOne(String query, Map<String, Object> params) {
        try {
            T result = jdbc.queryForObject(query, params, mapper);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    /**
     * Выполнить запрос, возвращающий список объектов.
     */
    protected List<T> findMany(String query, Map<String, Object> params) {
        return jdbc.query(query, params, mapper);
    }

    /**
     * Удалить запись по ID.
     */
    protected void delete(String query, long id) {
        Map<String, Object> params = Map.of("id", id);
        int rowsDeleted = jdbc.update(query, params);
        if (rowsDeleted == 0) {
            throw new NotFoundException("Не удалось удалить данные");
        }
    }

    /**
     * Обновить запись.
     */
    protected void update(String query, Map<String, Object> params, Boolean throwOnNoChanges) {
        int rowsUpdated = jdbc.update(query, params);
        if (throwOnNoChanges && rowsUpdated == 0) {
            throw new InternalServerException("Не удалось обновить данные");
        }
    }

    /**
     * Вставить запись и вернуть сгенерированный ID.
     */
    protected long insert(String query, Map<String, Object> params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(
                query,
                new MapSqlParameterSource(params),
                keyHolder
        );

        Long id = keyHolder.getKeyAs(Long.class);
        if (id != null) {
            return id;
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }
}
