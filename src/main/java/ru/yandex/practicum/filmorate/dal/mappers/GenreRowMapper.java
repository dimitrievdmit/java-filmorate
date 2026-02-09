package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.enums.FilmGenre;

import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("unused")
@Component
public class GenreRowMapper implements RowMapper<FilmGenre> {
    @Override
    public FilmGenre mapRow(ResultSet rs, int rowNum) throws SQLException {
        return FilmGenre.fromId(rs.getInt("id"));
    }
}
