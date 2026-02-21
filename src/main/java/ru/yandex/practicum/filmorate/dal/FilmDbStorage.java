package ru.yandex.practicum.filmorate.dal;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dal.mappers.FilmDirectorRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.FilmGenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.FilmLikeRowMapper;
import ru.yandex.practicum.filmorate.enums.FilmGenre;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
@Profile("db")  // аннотация @Qualifier в сервисах мешала настроить тесты сразу на обе реализации
public class FilmDbStorage extends BaseDBRepository<Film> implements FilmStorage {

    private final FilmGenreRowMapper filmGenreRowMapper;
    private final FilmLikeRowMapper filmLikeRowMapper;
    private final FilmDirectorRowMapper filmDirectorRowMapper;


    private static final String SELECT_ALL_FILMS_QUERY = """
                SELECT
                    f.id,
                    f.name,
                    f.description,
                    f.release_date,
                    f.duration,
                    f.rating_id
                FROM films f
            """;


    private static final String SELECT_ONE_FILM_QUERY = """
                SELECT
                    f.id,
                    f.name,
                    f.description,
                    f.release_date,
                    f.duration,
                    f.rating_id
                FROM films f
                WHERE f.id = :id
            """;

    private static final String SELECT_GENRES_QUERY = """
                SELECT
                    fg.film_id,
                    fg.genre_id
                FROM film_genres fg
                WHERE fg.film_id IN (:filmIds)
            """;

    private static final String SELECT_LIKES_QUERY = """
                SELECT
                    fl.film_id,
                    fl.user_id
                FROM film_likes fl
                WHERE fl.film_id IN (:filmIds)
            """;

    private static final String SELECT_DIRECTORS_QUERY = """
                SELECT
                    fd.film_id,
                    fd.director_id
                FROM film_director fd
                WHERE fd.film_id IN (:filmIds)
            """;

    private static final String SELECT_TOP_FILMS_QUERY = """
                SELECT
                    f.id,
                    f.name,
                    f.description,
                    f.release_date,
                    f.duration,
                    f.rating_id
                FROM films f
                LEFT JOIN film_likes fl ON f.id = fl.film_id
                GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.rating_id
                ORDER BY COUNT(fl.user_id) DESC
                LIMIT :count
            """;

    private static final String INSERT_QUERY = """
                INSERT INTO films (name, description, release_date, duration, rating_id)
                VALUES (:name, :description, :releaseDate, :duration, :ratingId)
            """;

    private static final String UPDATE_QUERY = """
                UPDATE films
                SET name = :name,
                    description = :description,
                    release_date = :releaseDate,
                    duration = :duration,
                    rating_id = :ratingId
                WHERE id = :id
            """;

    private static final String DELETE_QUERY = "DELETE FROM films WHERE id = :id";


    private static final String INSERT_LIKE_QUERY = """
                INSERT INTO film_likes (film_id, user_id)
                VALUES (:filmId, :userId)
            """;

    private static final String INSERT_GENRE_QUERY = """
                INSERT INTO film_genres (film_id, genre_id)
                VALUES (:filmId, :genreId)
            """;

    private static final String INSERT_DIRECTOR_QUERY = """
                INSERT INTO film_director (film_id, director_id)
                VALUES (:filmId, :director_id)
            """;

    private static final String DELETE_LIKES_QUERY = "DELETE FROM film_likes WHERE film_id = :filmId";
    private static final String DELETE_SINGLE_LIKE_QUERY = """
                DELETE FROM film_likes
                WHERE film_id = :filmId AND user_id = :userId
            """;

    private static final String DELETE_GENRES_QUERY = "DELETE FROM film_genres WHERE film_id = :filmId";
    private static final String DELETE_SINGLE_GENRE_QUERY = """
                DELETE FROM film_genres
                WHERE film_id = :filmId AND genre_id = :genreId
            """;
    private static final String DELETE_DIRECTOR_QUERY = "DELETE FROM film_director WHERE film_id = :filmId";
    private static final String DELETE_SINGLE_DIRECTOR_QUERY = """
                DELETE FROM film_director
                WHERE film_id = :filmId AND director_id = :genreId
            """;

    private static final String GET_DIRECTOR_FILMS_BY_ID = """
            SELECT
            f.id,
            f.name,
            f.description,
            f.release_date,
            f.duration,
            f.rating_id,
            fd.director_id
            FROM films f
            JOIN film_director fd ON fd.film_id = f.id
            WHERE fd.director_id = :directorId
            """;

    private static final String SELECT_FILM_DIRECTORS_BY_ID = """
            SELECT
            fd.director_id
            FROM film_director fd
            WHERE film_id = :filmId
            """;


    public FilmDbStorage(
            NamedParameterJdbcTemplate jdbc,
            RowMapper<Film> mapper,
            FilmGenreRowMapper filmGenreRowMapper,
            FilmLikeRowMapper filmLikeRowMapper,
            FilmDirectorRowMapper filmDirectorRowMapper
    ) {
        super(jdbc, mapper);
        this.filmGenreRowMapper = filmGenreRowMapper;
        this.filmLikeRowMapper = filmLikeRowMapper;
        this.filmDirectorRowMapper = filmDirectorRowMapper;
    }


    @Override
    public Collection<Film> getAllFilms() {
        // 1. Получаем основные данные о фильмах (без жанров и лайков)
        List<Film> films = findMany(SELECT_ALL_FILMS_QUERY, Collections.emptyMap());

        if (films.isEmpty()) {
            return films;
        }

        // 2. Получаем все жанры для указанных фильмов
        // 3. Получаем все лайки для указанных фильмов
        // 4. Дополняем каждый фильм собранными данными
        return getFilmAdditionalData(films);
    }

    @Override
    public Collection<Film> getPopularFilms(Long count) {
        // 1. Получаем основные данные о фильмах (без жанров и лайков)
        List<Film> films = findMany(SELECT_TOP_FILMS_QUERY, Map.of("count", count));

        if (films.isEmpty()) {
            return films;
        }

        // 2. Получаем все жанры для указанных фильмов
        // 3. Получаем все лайки для указанных фильмов
        // 4. Дополняем каждый фильм собранными данными
        getFilmAdditionalData(films);

        return films;
    }

    @Override
    public boolean checkIfNotExists(Long id) {
        return findOne(SELECT_ONE_FILM_QUERY, Map.of("id", id)).isEmpty();
    }

    @Override
    public Film getFilm(Long id) {
        // 1. Получаем основную информацию о фильме по ID
        Optional<Film> filmOpt = findOne(SELECT_ONE_FILM_QUERY, Map.of("id", id));

        if (filmOpt.isEmpty()) {
            return null;
        }

        Film film = filmOpt.get();

        // 2. Получаем жанры для этого фильма
        // 3. Получаем лайки для этого фильма
        // 4. Заполняем жанры и лайки в объекте фильма
        film = getFilmAdditionalData(List.of(film)).stream().findFirst().orElse(null);

        return film;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Film createFilm(Film film) {

        Map<String, Object> params = Map.of(
                "name", film.getName(),
                "description", film.getDescription(),
                "releaseDate", Timestamp.valueOf(film.getReleaseDate().atStartOfDay()),
                "duration", film.getDuration(),
                "ratingId", film.getRating().getId()
        );

        long filmId = insert(INSERT_QUERY, params);
        film.setId(filmId);

        updateFilmGenres(film, filmId, false);
        updateFilmLikes(film, filmId, false);
        updateFilmDirectors(film, filmId, false);
        return film;
    }

    /**
     * Обновляет жанры фильма в БД.
     * При reset=true сначала удаляет все существующие жанры, затем добавляет новые.
     * При reset=false только добавляет новые (без удаления).
     */
    private void updateFilmGenres(Film film, long filmId, Boolean reset) {
        if (reset) {
            // 1. Удаляем все существующие жанры для данного фильма
            update(DELETE_GENRES_QUERY, Map.of("filmId", filmId), false);
        }
        // 2. Если жанры указаны — добавляем их в БД
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            // Формируем массив параметров для каждого жанра
            SqlParameterSource[] batch = film.getGenres().stream()
                    .map(genre -> new MapSqlParameterSource()
                            .addValue("filmId", filmId)
                            .addValue("genreId", genre.getId()))  // Берём ID из enum
                    .toArray(SqlParameterSource[]::new);

            // Выполняем batch-вставку
            jdbc.batchUpdate(INSERT_GENRE_QUERY, batch);
        }
    }

    private void updateFilmDirectors(Film film, long filmId, Boolean reset) {
        if (reset) {

            // 1. Удаляем всех существующих режиссеров для данного фильма
            update(DELETE_DIRECTOR_QUERY, Map.of("filmId", filmId), false);

        }// 2. Если режиссеры указаны — добавляем их в БД
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            // Формируем массив параметров для каждого режиссера
            SqlParameterSource[] batch = film.getDirectors().stream()
                    .map(director -> new MapSqlParameterSource()
                            .addValue("filmId", filmId)
                            .addValue("director_id", director))
                    .toArray(SqlParameterSource[]::new);

            // Выполняем batch-вставку
            jdbc.batchUpdate(INSERT_DIRECTOR_QUERY, batch);
        }
    }

    /**
     * Обновляет лайки фильма в БД.
     * При reset=true сначала удаляет все существующие лайки, затем добавляет новые.
     * При reset=false только добавляет новые (без удаления).
     */
    private void updateFilmLikes(Film film, long filmId, Boolean reset) {
        if (reset) {
            update(DELETE_LIKES_QUERY, Map.of("filmId", filmId), false);
        }

        if (film.getLikes() != null && !film.getLikes().isEmpty()) {
            // Формируем массив параметров для каждого лайка
            SqlParameterSource[] batch = film.getLikes().stream()
                    .map(userId -> new MapSqlParameterSource()
                            .addValue("filmId", filmId)
                            .addValue("userId", userId))
                    .toArray(SqlParameterSource[]::new);

            // Выполняем batch-вставку
            jdbc.batchUpdate(INSERT_LIKE_QUERY, batch);
        }
    }

    @Override
    public Film filmAddLike(Long id, Long userId) {
        Film film = getFilm(id);
        Map<String, Object> params = Map.of(
                "filmId", id,
                "userId", userId
        );
        update(INSERT_LIKE_QUERY, params, true);
        film.addLike(userId);
        return film;
    }

    @Override
    public Film removeLike(Long id, Long userId) {
        Film film = getFilm(id);
        Map<String, Object> params = Map.of(
                "filmId", id,
                "userId", userId
        );
        update(DELETE_SINGLE_LIKE_QUERY, params, true);
        film.removeLike(userId);
        return film;
    }

    @Override
    public Film filmAddGenre(Long id, Integer genreId) {
        Film film = getFilm(id);
        Map<String, Object> params = Map.of(
                "filmId", id,
                "genreId", genreId
        );
        update(INSERT_GENRE_QUERY, params, true);
        film.addGenre(FilmGenre.fromId(genreId));
        return film;
    }

    @Override
    public Film removeGenre(Long id, Integer genreId) {
        Film film = getFilm(id);
        Map<String, Object> params = Map.of(
                "filmId", id,
                "genreId", genreId
        );
        update(DELETE_SINGLE_GENRE_QUERY, params, true);
        film.removeGenre(FilmGenre.fromId(genreId));
        return film;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Film updateFilm(Film newFilm) {
        long filmId = newFilm.getId();

        // 1. Обновляем основную запись фильма (без жанров и лайков)
        Map<String, Object> updateParams = Map.of(
                "name", newFilm.getName(),
                "description", newFilm.getDescription(),
                "releaseDate", Timestamp.valueOf(newFilm.getReleaseDate().atStartOfDay()),
                "duration", newFilm.getDuration(),
                "ratingId", newFilm.getRating().getId(),
                "id", filmId
        );
        update(UPDATE_QUERY, updateParams, true);

        // 2. Перезаписываем жанры: сначала удаляем старые, затем добавляем новые
        updateFilmGenres(newFilm, filmId, true);

        // 3. Перезаписываем лайки: сначала удаляем старые, затем добавляем новые
        updateFilmLikes(newFilm, filmId, true);

        // 4. Перезаписываем режиссеров
        updateFilmDirectors(newFilm, filmId, true);

        return newFilm;
    }

    @Transactional(rollbackFor = Throwable.class)
    public List<Film> getDirectorFilms(long directorId) {

        MapSqlParameterSource params = new MapSqlParameterSource("directorId", directorId);
        List<Film> films = jdbc.query(GET_DIRECTOR_FILMS_BY_ID, params, mapper);
        List<Long> filmIds = films.stream()
                .map(Film::getId)
                .toList();
        Map<Long, Set<Long>> filmDirectors = this.getFilmDirectors(filmIds);

        return films.stream()
                .peek(f -> f.setDirectors(filmDirectors.get(f.getId())))
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void deleteFilm(Long id) {
        // Благодаря ON DELETE CASCADE у всех внешних ключей, явное удаление лайков, жанров и режиссеров не нужно.
        delete(DELETE_QUERY, id);
    }

    private Collection<Film> getFilmAdditionalData(List<Film> films) {
        // 1. Собираем ID фильмов для подзапросов
        List<Long> filmIds = films.stream()
                .map(Film::getId)
                .toList();

        // 2. Получаем жанры для указанных фильмов
        Map<Long, Set<FilmGenre>> genresMap = getFilmGenres(filmIds);

        // 3. Получаем лайки для указанных фильмов
        Map<Long, Set<Long>> likesMap = getFilmLikes(filmIds);

        Map<Long, Set<Long>> directorMap = getFilmDirectors(filmIds);
        // 4. Создаём новые объекты Film с дополненными данными (не меняя исходные)
        return enrichFilms(films, genresMap, likesMap, directorMap);
    }

    private Map<Long, Set<FilmGenre>> getFilmGenres(List<Long> filmIds) {
        // Получаем все жанры для указанных фильмов
        // Запрос возвращает пары (film_id, genre_id)
        return jdbc.query(
                        SELECT_GENRES_QUERY,
                        Map.of("filmIds", filmIds),
                        filmGenreRowMapper
                )
                .stream()
                // Группируем по film_id: для каждого фильма — набор жанров
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toSet())
                ));
    }

    private Map<Long, Set<Long>> getFilmLikes(List<Long> filmIds) {
        // Получаем все лайки для указанных фильмов
        // Запрос возвращает пары (film_id, user_id)
        return jdbc.query(
                        SELECT_LIKES_QUERY,
                        Map.of("filmIds", filmIds),
                        filmLikeRowMapper
                )
                .stream()
                // Группируем по film_id: для каждого фильма — набор ID пользователей, поставивших лайк
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toSet())
                ));
    }

    private Map<Long, Set<Long>> getFilmDirectors(List<Long> filmIds) {
        // Получаем всех режиссеров для указанных фильмов
        // Запрос возвращает пары (film_id, user_id)
        Map<Long, Set<Long>> get = jdbc.query(
                        SELECT_DIRECTORS_QUERY,
                        Map.of("filmIds", filmIds),
                        filmDirectorRowMapper
                )
                .stream()
                .peek(System.out::println)
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toSet())
                ));
        System.out.println("1111" + get);
        return get;
    }

    private List<Film> enrichFilms(
            List<Film> films,
            Map<Long, Set<FilmGenre>> genresMap,
            Map<Long, Set<Long>> likesMap,
            Map<Long, Set<Long>> directorMap
    ) {
        System.out.println("2222"+directorMap);
        return films.stream()
                .sorted(Comparator.comparing(Film::getId))
                .map(film -> {
                    Film newFilm = new Film();
                    // Копируем все поля из исходного фильма
                    newFilm.setId(film.getId());
                    newFilm.setName(film.getName());
                    newFilm.setDescription(film.getDescription());
                    newFilm.setReleaseDate(film.getReleaseDate());
                    newFilm.setDuration(film.getDuration());
                    newFilm.setRating(film.getRating());
                    newFilm.setDirectors(directorMap.get(film.getId()));

                    // Добавляем дополнительные данные
                    newFilm.setGenres(genresMap.getOrDefault(film.getId(), new HashSet<>()));
                    newFilm.setLikes(likesMap.getOrDefault(film.getId(), new HashSet<>()));
                    return newFilm;
                })
                .collect(Collectors.toList());
    }
}
