package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.ReviewRating;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@Profile("db")
public class ReviewRatingRowMapper implements RowMapper<ReviewRating> {
    @Override
    public ReviewRating mapRow(ResultSet rs, int rowNum) throws SQLException {
        final var rating = new ReviewRating();
        rating.setReviewId(rs.getLong("review_id"));
        rating.setUserId(rs.getLong("user_id"));
        rating.setPositive(rs.getBoolean("positive"));
        return rating;
    }
}