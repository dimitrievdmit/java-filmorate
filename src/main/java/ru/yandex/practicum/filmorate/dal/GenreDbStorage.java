package ru.yandex.practicum.filmorate.dal;


import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.enums.FilmGenre;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;


@SuppressWarnings("unused")
@Repository
public class GenreDbStorage extends BaseDBRepository<FilmGenre> implements GenreStorage {

    private static final String SELECT_ALL_GENRES_QUERY = """
                SELECT
                    g.id,
                    g.name
                FROM genres g
                ORDER BY g.id ASC
            """;

    private static final String SELECT_ONE_GENRE_QUERY = """
                SELECT
                    g.id,
                    g.name
                FROM genres g
                WHERE g.id = :id
            """;

    public GenreDbStorage(
            NamedParameterJdbcTemplate jdbc,
            RowMapper<FilmGenre> mapper
    ) {
        super(jdbc, mapper);
    }


    @Override
    public Collection<FilmGenre> getAll() {
        return findMany(SELECT_ALL_GENRES_QUERY, Collections.emptyMap());
    }

    @Override
    public FilmGenre getOne(Integer id) {
        Optional<FilmGenre> genreOpt = findOne(SELECT_ONE_GENRE_QUERY, Map.of("id", id));
        return genreOpt.orElse(null);

    }

}
