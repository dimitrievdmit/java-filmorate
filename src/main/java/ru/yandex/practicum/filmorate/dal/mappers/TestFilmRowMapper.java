package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.enums.FilmGenre;
import ru.yandex.practicum.filmorate.enums.FilmRating;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
@Component
public class TestFilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getObject("release_date", LocalDate.class));
        film.setDuration(rs.getLong("duration"));

        Integer ratingId = rs.getInt("rating_id");
        FilmRating rating = FilmRating.fromId(ratingId);
        film.setRating(rating);

        // Преобразуем массив жанров (genre_id → FilmGenre)
        Array genreArray = rs.getArray("genres");
        Set<FilmGenre> genres = new HashSet<>();
        if (genreArray != null) {
            Object[] genreObjects = (Object[]) genreArray.getArray();
            for (Object genreObj : genreObjects) {
                if (genreObj != null) {
                    // Приводим Object к Integer
                    Integer genreId = (Integer) genreObj;
                    FilmGenre genre = FilmGenre.fromId(genreId);
                    // Проверяем, что жанр найден
                    genres.add(genre);
                }
            }
        }
        film.setGenres(genres);

        // Преобразуем массив лайков (user_id → Set<Long>)
        Array likeArray = rs.getArray("likes");
        if (likeArray != null) {
            Object[] likeObjects = (Object[]) likeArray.getArray();
            Set<Long> likes = new HashSet<>();
            for (Object likeObj : likeObjects) {
                if (likeObj != null) {
                    Long likeId = (Long) likeObj;
                    likes.add(likeId);
                }
            }
            film.setLikes(likes);
        } else {
            film.setLikes(new HashSet<>());
        }


        return film;
    }
}
