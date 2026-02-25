package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.Map;

@Component
public class FilmDirectorRowMapper implements RowMapper<Map.Entry<Long, Director>> {

    @Override
        public Map.Entry<Long, Director> mapRow(ResultSet rs, int rowNum) throws SQLException {
            Long filmId = rs.getLong("film_id");
            Long directorId = rs.getLong("director_id");
            String directorName = rs.getString("director_name");

            Director director = new Director(directorId, directorName);
            return new AbstractMap.SimpleEntry<>(filmId, director);
        }
    }

