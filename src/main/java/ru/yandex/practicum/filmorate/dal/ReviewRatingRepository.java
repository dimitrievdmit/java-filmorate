package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.ReviewRating;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@Repository
public class ReviewRatingRepository extends BaseDBRepository<ReviewRating> {

    private static final String ADD_LIKE_QUERY =
            "INSERT INTO review_rating (user_id, review_id, positive) VALUES (:userId, :reviewId, true)";

    private static final String ADD_DISLIKE_QUERY =
            "INSERT INTO review_rating (user_id, review_id, positive) VALUES (:userId, :reviewId, false)";

    private static final String REMOVE_LIKE_QUERY =
            "DELETE FROM review_rating WHERE review_id = :reviewId AND user_id = :userId";

    private static final String EXISTS_QUERY =
            "SELECT EXISTS(SELECT 1 FROM review_rating WHERE review_id = :reviewId AND user_id = :userId)";

    private static final String GET_USEFUL_QUERY =
            "SELECT COALESCE(SUM(CASE WHEN positive THEN 1 ELSE -1 END), 0) " +
                    "FROM review_rating WHERE review_id = :reviewId";

    private static final String GET_USEFUL_BY_IDS_QUERY =
            "SELECT review_id, COALESCE(SUM(CASE WHEN positive THEN 1 ELSE -1 END), 0) AS useful " +
                    "FROM review_rating WHERE review_id IN (:reviewIds) GROUP BY review_id";

    public ReviewRatingRepository(NamedParameterJdbcTemplate jdbc, RowMapper<ReviewRating> mapper) {
        super(jdbc, mapper);
    }

    public void addLike(long reviewId, long userId) {
        jdbc.update(ADD_LIKE_QUERY, Map.of("reviewId", reviewId, "userId", userId));
    }

    public void addDislike(long reviewId, long userId) {
        jdbc.update(ADD_DISLIKE_QUERY, Map.of("reviewId", reviewId, "userId", userId));
    }

    public void removeVote(long reviewId, long userId) {
        jdbc.update(REMOVE_LIKE_QUERY, Map.of("reviewId", reviewId, "userId", userId));
    }

    public Boolean exists(long reviewId, long userId) {
        return jdbc.queryForObject(EXISTS_QUERY, Map.of("reviewId", reviewId, "userId", userId), Boolean.class);
    }

    public Integer getUseful(long reviewId) {
        return jdbc.queryForObject(GET_USEFUL_QUERY, Map.of("reviewId", reviewId), Integer.class);
    }

    public Map<Long, Integer> getUsefulByReviewIds(List<Long> reviewIds) {
        if (reviewIds.isEmpty()) {
            return Map.of();
        }
        Map<Long, Integer> result = new HashMap<>();
        jdbc.query(GET_USEFUL_BY_IDS_QUERY, Map.of("reviewIds", reviewIds), rs -> {
            result.put(rs.getLong("review_id"), rs.getInt("useful"));
        });
        return result;
    }
}