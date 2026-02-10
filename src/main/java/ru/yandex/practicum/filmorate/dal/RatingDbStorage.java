package ru.yandex.practicum.filmorate.dal;


import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.enums.FilmRating;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;


@Repository
@Profile("db")  // аннотация @Qualifier в сервисах мешала настроить тесты сразу на обе реализации
public class RatingDbStorage extends BaseDBRepository<FilmRating> implements RatingStorage {

    private static final String SELECT_ALL_RATINGS_QUERY = """
                SELECT
                    f.id,
                    f.name
                FROM film_rating f
            """;

    private static final String SELECT_ONE_RATING_QUERY = """
                SELECT
                    f.id,
                    f.name
                FROM film_rating f
                WHERE f.id = :id
            """;

    public RatingDbStorage(
            NamedParameterJdbcTemplate jdbc,
            RowMapper<FilmRating> mapper
    ) {
        super(jdbc, mapper);
    }


    @Override
    public Collection<FilmRating> getAll() {
        return findMany(SELECT_ALL_RATINGS_QUERY, Collections.emptyMap());
    }

    @Override
    public FilmRating getOne(Integer id) {
        Optional<FilmRating> ratingOpt = findOne(SELECT_ONE_RATING_QUERY, Map.of("id", id));
        return ratingOpt.orElse(null);

    }

}
