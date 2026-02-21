package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.Map;

@Component
public class FilmDirectorRowMapper implements RowMapper<Map.Entry<Long, Long>> {

    @Override
        public Map.Entry<Long, Long> mapRow(ResultSet rs, int rowNum) throws SQLException {
            Long filmId = rs.getLong("film_id");
            Long directorId = rs.getLong("director_id");
            return new AbstractMap.SimpleEntry<>(filmId, directorId);
        }
    }

