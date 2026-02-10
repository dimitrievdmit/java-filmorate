package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.enums.FilmGenre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.Map;

@Component
public class FilmGenreRowMapper implements RowMapper<Map.Entry<Long, FilmGenre>> {
    @Override
    public Map.Entry<Long, FilmGenre> mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long filmId = rs.getLong("film_id");
        FilmGenre genre = FilmGenre.fromId(rs.getInt("genre_id"));
        return new AbstractMap.SimpleEntry<>(filmId, genre);
    }
}

