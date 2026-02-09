package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.enums.FilmRating;

import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("unused")
@Component
public class RatingRowMapper implements RowMapper<FilmRating> {
    @Override
    public FilmRating mapRow(ResultSet rs, int rowNum) throws SQLException {
        return FilmRating.fromId(rs.getInt("id"));
    }
}
