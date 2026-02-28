package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unused")
@Repository
public class ReviewRepository extends BaseDBRepository<Review> {

    private static final String INSERT_QUERY =
            "INSERT INTO reviews (user_id, film_id, content, positive) " +
                    "VALUES (:userId, :filmId, :content, :positive)";

    private static final String UPDATE_QUERY =
            "UPDATE reviews SET content = :content, positive = :positive WHERE id = :id";

    private static final String DELETE_QUERY = "DELETE FROM reviews WHERE id = :id";

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM reviews WHERE id = :id";

    private static final String EXISTS_BY_ID_QUERY =
            "SELECT EXISTS(SELECT 1 FROM reviews WHERE id = :id)";

    private static final String FIND_ALL_QUERY =
            "SELECT * FROM reviews LIMIT :count";

    private static final String FIND_BY_FILM_ID_QUERY =
            "SELECT * FROM reviews WHERE film_id = :filmId LIMIT :count";

    public ReviewRepository(NamedParameterJdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    public Review create(Review review) {
        long id = insert(INSERT_QUERY, Map.of(
                "userId", review.getUserId(),
                "filmId", review.getFilmId(),
                "content", review.getContent(),
                "positive", review.getPositive()
        ));
        review.setId(id);
        return review;
    }

    public Optional<Review> updateById(long id, String content, Boolean positive) {
        update(UPDATE_QUERY, Map.of(
                "id", id,
                "content", content,
                "positive", positive
        ), false);
        return findById(id);
    }

    public void deleteById(long id) {
        delete(DELETE_QUERY, id);
    }

    public Optional<Review> findById(long id) {
        return findOne(FIND_BY_ID_QUERY, Map.of("id", id));
    }

    @SuppressWarnings("unused")
    public Boolean existsById(long id) {
        return jdbc.queryForObject(EXISTS_BY_ID_QUERY, Map.of("id", id), Boolean.class);
    }

    public List<Review> findAll(int count) {
        return findMany(FIND_ALL_QUERY, Map.of("count", count));
    }

    public List<Review> findByFilmId(long filmId, int count) {
        return findMany(FIND_BY_FILM_ID_QUERY, Map.of("filmId", filmId, "count", count));
    }
}