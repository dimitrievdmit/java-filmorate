package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import ru.yandex.practicum.filmorate.dal.FilmStorage;
import ru.yandex.practicum.filmorate.dal.mappers.TestFilmRowMapper;
import ru.yandex.practicum.filmorate.mock.MockFilms;

import java.util.Collections;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmSingleOrManyRequestBenchTest {

    private final FilmService filmService;
    private final FilmStorage filmStorage;
    private final NamedParameterJdbcTemplate jdbc;
    private final TestFilmRowMapper mapper;

    private void createManyFilms(Long filmCount) {
        for (long i = 0L; i < filmCount; i++) {
            filmService.createFilm(MockFilms.getValidFilm());
        }
    }

    private void getAllFilmsOneRequest() {
        String sql = """
                        SELECT
                            f.id,
                            f.name,
                            f.description,
                            f.release_date,
                            f.duration,
                            f.rating_id,
                            COALESCE(array_agg(fg.genre_id), '{}') AS genres,
                            COALESCE(array_agg(fl.user_id), '{}') AS likes
                        FROM films f
                        LEFT JOIN film_genres fg ON f.id = fg.film_id
                        LEFT JOIN film_likes fl ON f.id = fl.film_id
                        GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.rating_id;
                """;

        jdbc.query(sql, Collections.emptyMap(), mapper);
    }

    @Test
    public void singleShouldBeFasterThanMany() {
        createManyFilms(10000L);
//        createManyFilms(1L);

        long t, t1 = 0, t2 = 0;

        for (int i = 0; i < 50; i++) {
            t = System.currentTimeMillis();
            filmStorage.getAllFilms();
            t1 += System.currentTimeMillis() - t;


            t = System.currentTimeMillis();
            getAllFilmsOneRequest();
            t2 += System.currentTimeMillis() - t;
        }


        System.out.println("Benchmarking\n\tgetAllFilms took + " + t1 + " ms" +
                "\n\tgetAllFilmsOneRequest took " + t2 + " ms");
    }

}