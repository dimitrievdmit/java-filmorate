package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.*;

@Slf4j
@Repository
public class DirectorDBStorage extends BaseDBRepository<Director> implements DirectorStorage{

    public DirectorDBStorage(NamedParameterJdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    private static final String INSERT_DIRECTOR = "INSERT INTO directors (name) VALUES (:name)";
    private static final String UPDATE_DIRECTOR_BY_ID = "UPDATE directors SET name = :name WHERE id = :id";
    private static final String DELETE_DIRECTOR_BY_ID = "DELETE directors WHERE id = :id";
    private static final String SELECT_DIRECTOR_BY_ID = "SELECT * FROM directors WHERE id = :id";
    private static final String SELECT_ALL_DIRECTORS = "SELECT * FROM directors";
    //работает только с H2 из-за MERGE
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
        int row = jdbc.update(UPDATE_DIRECTOR_BY_ID, params);
        if (row == 0) throw new NotFoundException("Режиссер с id " + director.getId() + " не найден");
    }

    @Override
    public void deleteDirector(long id) {
        delete(DELETE_DIRECTOR_BY_ID, id);
    }

    public Optional<Director> getDirector(long id) {
        Map<String, Object> params = Map.of("id", id);
        return findOne(SELECT_DIRECTOR_BY_ID, params);
    }

    public List<Director> getAllDirectors(){
        return findMany(SELECT_ALL_DIRECTORS, Collections.emptyMap());
    }

    @Override
    public boolean checkIfNotExists(Long id) {
        Map<String, Object> params = Map.of("id", id);
        return findOne(SELECT_DIRECTOR_BY_ID, params).isEmpty();
    }
}
