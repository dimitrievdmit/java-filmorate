package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.Array;
import java.util.*;

@Slf4j
@Repository
public class DirectorDBStorage extends BaseDBRepository<Director> implements DirectorStorage {

    public DirectorDBStorage(NamedParameterJdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    private static final String INSERT_DIRECTOR = "INSERT INTO directors (name) VALUES (:name)";

    private static final String UPDATE_DIRECTOR_BY_ID = "UPDATE directors SET name = :name WHERE id = :id";

    private static final String DELETE_DIRECTOR_BY_ID = "DELETE FROM directors WHERE id = :id";

    private static final String SELECT_DIRECTOR_BY_ID = "SELECT * FROM directors WHERE id = :id";

    private static final String SELECT_ALL_DIRECTORS = "SELECT * FROM directors ORDER BY id ASC";

    private static final String INSERT_DIRECTOR_QUERY = """
                INSERT INTO film_director (film_id, director_id)
                VALUES (:filmId, :director_id)
            """;

    private static final String DELETE_DIRECTOR_QUERY = "DELETE FROM film_director WHERE film_id = :filmId";

    private static final String SELECT_ONE_DIRECTOR_QUERY = """
                SELECT
                    d.id,
                    d.name
                FROM directors d
                WHERE d.id = :id
            """;

    @Override
    public Director createDirector(Director director) {

        Map<String, Object> params = Map.of("name", director.getName());
        long id = insert(INSERT_DIRECTOR, params);
        director.setId(id);

        log.debug("Режиссер id:{} создан", id);
        return director;
    }

    @Override
    public void updateDirector(Director director) {

        Map<String, Object> params = Map.of(
                "id", director.getId(),
                "name", director.getName()
        );
        update(UPDATE_DIRECTOR_BY_ID, params, true);
    }

    @Override
    public void deleteDirector(long id) {
        delete(DELETE_DIRECTOR_BY_ID, id);
    }

    public Optional<Director> getDirector(long id) {
        Map<String, Object> params = Map.of("id", id);
        return findOne(SELECT_DIRECTOR_BY_ID, params);
    }

    public List<Director> getAllDirectors() {
        return jdbc.query(SELECT_ALL_DIRECTORS, mapper);
    }

    @Override
    public boolean checkIfNotExists(Long id) {
        Map<String, Object> params = Map.of("id", id);
        return findOne(SELECT_DIRECTOR_BY_ID, params).isEmpty();
    }

    @Override
    public void updateDirectorsForFilm(Set<Long> directorIds, long filmId, Boolean reset) {
        if (reset) {

            // 1. Удаляем всех существующих режиссеров для данного фильма
            update(DELETE_DIRECTOR_QUERY, Map.of("filmId", filmId), false);
        }   // 2. Если режиссеры указаны — добавляем их в БД
        if (directorIds != null && !directorIds.isEmpty()) {
            // Формируем массив параметров для каждого режиссера
            SqlParameterSource[] batch = directorIds.stream()
                    .map(id -> new MapSqlParameterSource()
                            .addValue("filmId", filmId)
                            .addValue("director_id", id))
                    .toArray(SqlParameterSource[]::new);

            // Выполняем batch-вставку
            jdbc.batchUpdate(INSERT_DIRECTOR_QUERY, batch);

        }
    }

    @Override
    public boolean checkIfDirectorNotExists(Long id) {
        return jdbc.query(SELECT_ONE_DIRECTOR_QUERY, Map.of("id", id), new DirectorRowMapper()).isEmpty();
    }
}
