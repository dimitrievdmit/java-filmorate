package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dal.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.FilmDirectorRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.FilmGenreRowMapper;
import ru.yandex.practicum.filmorate.enums.FilmGenre;
import ru.yandex.practicum.filmorate.enums.FilmSearchType;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class FilmDbStorage extends BaseDBRepository<Film> implements FilmStorage {

    private final LikeStorage likeStorage;
    private final FilmGenreRowMapper filmGenreRowMapper;
    private final FilmDirectorRowMapper filmDirectorRowMapper;

    private static final String SELECT_ALL_FILMS_QUERY = """
                SELECT
                f.id,
                f.name,
                f.description,
                f.release_date,
                f.duration,
                f.rating_id,
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

    private static final String SELECT_ONE_DIRECTOR_QUERY = """
                SELECT
                    d.id,
                    d.name
                FROM directors d
                WHERE d.id = :id
            """;

    private static final String SELECT_MANY_FILMS_QUERY = """
                SELECT
                    f.id,
                    f.name,
                    f.description,
                    f.release_date,
                    f.duration,
                    f.rating_id
                FROM films f
                WHERE f.id IN (:filmIds)
            """;

    private static final String SELECT_GENRES_QUERY = """
                SELECT
                    fg.film_id,
                    fg.genre_id
                FROM film_genres fg
                WHERE fg.film_id IN (:filmIds)
            """;

    private static final String SELECT_TOP_FILMS_WITH_FILTERS_QUERY = """
            SELECT
                f.id,
                f.name,
                f.description,
                f.release_date,
                f.duration,
                f.rating_id
            FROM films f
            LEFT JOIN film_likes fl ON f.id = fl.film_id
            LEFT JOIN film_genres fg ON f.id = fg.film_id
            WHERE (:genreId IS NULL OR fg.genre_id = :genreId)
              AND (:year IS NULL OR EXTRACT(YEAR FROM f.release_date) = :year)
            GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.rating_id
            ORDER BY COUNT(fl.user_id) DESC
            LIMIT :count
            """;

    private static final String SELECT_DIRECTORS_QUERY = """
                SELECT
                    fd.film_id,
                    fd.director_id,
                    d.name as director_name
                FROM film_director fd
                JOIN directors d ON fd.director_id = d.id
                WHERE fd.film_id IN (:filmIds)
            """;

    private static final String SELECT_TOP_FILMS_BY_TITLE_AND_DIRECTOR_NAME_QUERY = """
                SELECT
                    f.id,
                    f.name,
                    f.description,
                    f.release_date,
                    f.duration,
                    f.rating_id
                FROM films f
                LEFT JOIN film_likes fl ON f.id = fl.film_id
                LEFT JOIN (
                    SELECT fd.film_id, d.name AS director_name
                    FROM film_director fd
                    JOIN directors d ON fd.director_id = d.id
                ) AS director_data ON f.id = director_data.film_id
                WHERE (:filmName IS NOT NULL AND LOWER(f.name) LIKE :filmName)
                   OR (:directorName IS NOT NULL AND LOWER(director_data.director_name) LIKE :directorName)
                GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.rating_id
                ORDER BY COUNT(fl.user_id) DESC;
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

    private static final String INSERT_GENRE_QUERY = """
                INSERT INTO film_genres (film_id, genre_id)
                VALUES (:filmId, :genreId)
            """;

    private static final String INSERT_DIRECTOR_QUERY = """
                INSERT INTO film_director (film_id, director_id)
                VALUES (:filmId, :director_id)
            """;

    private static final String DELETE_GENRES_QUERY = "DELETE FROM film_genres WHERE film_id = :filmId";
    private static final String DELETE_SINGLE_GENRE_QUERY = """
                DELETE FROM film_genres
                WHERE film_id = :filmId AND genre_id = :genreId
            """;

    private static final String DELETE_DIRECTOR_QUERY = "DELETE FROM film_director WHERE film_id = :filmId";
    @SuppressWarnings("unused")
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

    @SuppressWarnings("unused")
    private static final String SELECT_FILM_DIRECTORS_BY_ID = """
            SELECT
            fd.director_id
            d.name as director_name
            FROM film_director fd
            JOIN directors d ON fd.director_id = d.id
            WHERE film_id = :filmId
            """;

    private static final String SELECT_COMMON_FILMS_WITH_POPULARITY = """
            SELECT
            f.id,
            f.name,
            f.description,
            f.release_date,
            f.duration,
            f.rating_id
            FROM films f
            JOIN film_likes fl1 ON f.id = fl1.film_id AND fl1.user_id = :userId
            JOIN film_likes fl2 ON f.id = fl2.film_id AND fl2.user_id = :friendId
            LEFT JOIN film_likes fl ON f.id = fl.film_id
            GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.rating_id
            ORDER BY COUNT(fl.user_id) DESC
            """;

    private static final String SELECT_RECOMMENDED_FILMS = """
            SELECT
                film.id,
                film.name,
                film.description,
                film.release_date,
                film.duration,
                film.rating_id
            FROM (
                SELECT
                    recommended_film.film_id,
                    COUNT(DISTINCT similar_user.user_id) AS similarity_based_popularity
                FROM film_likes target_user_likes
                JOIN film_likes similar_user ON target_user_likes.film_id = similar_user.film_id
                JOIN film_likes recommended_film ON similar_user.user_id = recommended_film.user_id
                LEFT JOIN film_likes already_liked_by_target ON recommended_film.film_id = already_liked_by_target.film_id
                    AND already_liked_by_target.user_id = :targetUser
                WHERE
                    target_user_likes.user_id = :targetUser
                    AND similar_user.user_id != :targetUser
                    AND already_liked_by_target.film_id IS NULL
                GROUP BY recommended_film.film_id
            ) AS film_recommendation_metrics
            JOIN films film ON film_recommendation_metrics.film_id = film.id
            ORDER BY film_recommendation_metrics.similarity_based_popularity DESC, film.id
            LIMIT :count
            """;


    @SuppressWarnings("unused")
    public FilmDbStorage(
            NamedParameterJdbcTemplate jdbc,
            RowMapper<Film> mapper,
            FilmGenreRowMapper filmGenreRowMapper,
            LikeStorage likeStorage,
            FilmDirectorRowMapper filmDirectorRowMapper
    ) {
        super(jdbc, mapper);
        this.filmGenreRowMapper = filmGenreRowMapper;
        this.likeStorage = likeStorage;
        this.filmDirectorRowMapper = filmDirectorRowMapper;
    }

    private Collection<Film> getManyFilmsWithAdditionalData(String query, Map<String, Object> params) {
        // Получаем основные данные о фильмах (без жанров и лайков)
        List<Film> films = findMany(query, params);

        if (films.isEmpty()) {
            return films;
        }

        // Получаем жанры для указанных фильмов
        // Получаем лайки для указанных фильмов
        // Получаем режиссёров для указанных фильмов
        // Дополняем каждый фильм собранными данными
        return getFilmAdditionalData(films);
    }

    @Override
    public Collection<Film> getAllFilms() {
        return getManyFilmsWithAdditionalData(SELECT_ALL_FILMS_QUERY, Collections.emptyMap());
    }

    @Override
    public Collection<Film> getPopularFilms(Long count, Integer genreId, Integer year) {
        Map<String, Object> params = new HashMap<>();
        params.put("count", count);
        params.put("genreId", genreId);
        params.put("year", year);

        return getManyFilmsWithAdditionalData(SELECT_TOP_FILMS_WITH_FILTERS_QUERY, params);
    }

    @Override
    public Collection<Film> getFilmsByTitleAndDirectorName(String query, FilmSearchType filmSearchType) {
        Map<String, Object> params = new HashMap<>();
        String queryPattern = '%' + query.toLowerCase(Locale.ROOT) + '%';
        switch (filmSearchType) {
            case TITLE:
                params.put("filmName", queryPattern);
                params.put("directorName", null);
                break;
            case DIRECTOR:
                params.put("filmName", null);
                params.put("directorName", queryPattern);
                break;
            case TITLE_AND_DIRECTOR:
                params.put("filmName", queryPattern);
                params.put("directorName", queryPattern);
                break;
            default:
                throw new IllegalArgumentException("Некорректное значение параметра типа поиска filmSearchType.");
        }

        return getManyFilmsWithAdditionalData(SELECT_TOP_FILMS_BY_TITLE_AND_DIRECTOR_NAME_QUERY, params);
    }

    @Override
    public Collection<Film> getRecommendedFilms(Long userId, Long count) {
        Map<String, Object> params = new HashMap<>();
        params.put("targetUser", userId);
        params.put("count", count);

        return getManyFilmsWithAdditionalData(SELECT_RECOMMENDED_FILMS, params);
    }

    @Override
    public boolean checkIfFilmNotExists(Long id) {
        return findOne(SELECT_ONE_FILM_QUERY, Map.of("id", id)).isEmpty();
    }

    @Override
    public boolean checkIfDirectorNotExists(Long id) {
        return jdbc.query(SELECT_ONE_DIRECTOR_QUERY, Map.of("id", id), new DirectorRowMapper()).isEmpty();
    }

    @Override
    public Collection<Film> getFilms(List<Long> filmIds) {
        return getManyFilmsWithAdditionalData(SELECT_MANY_FILMS_QUERY, Map.of("filmIds", filmIds));
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
        likeStorage.updateFilmLikes(film, false);
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
        }   // 2. Если режиссеры указаны — добавляем их в БД
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            // Формируем массив параметров для каждого режиссера
            SqlParameterSource[] batch = film.getDirectors().stream()
                    .map(director -> new MapSqlParameterSource()
                            .addValue("filmId", filmId)
                            .addValue("director_id", director.getId()))
                    .toArray(SqlParameterSource[]::new);

            // Выполняем batch-вставку
            jdbc.batchUpdate(INSERT_DIRECTOR_QUERY, batch);
        }
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
        likeStorage.updateFilmLikes(newFilm, true);

        // 4. Перезаписываем режиссеров
        updateFilmDirectors(newFilm, filmId, true);

        return newFilm;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public List<Film> getDirectorFilms(long directorId) {

        MapSqlParameterSource params = new MapSqlParameterSource("directorId", directorId);
        List<Film> films = jdbc.query(GET_DIRECTOR_FILMS_BY_ID, params, mapper);
        List<Long> filmIds = films.stream()
                .map(Film::getId)
                .toList();
        // Получаем жанры для фильмов
        Map<Long, Set<FilmGenre>> genresMap = this.getFilmGenres(filmIds);
        // Получаем лайки для фильмов
        Map<Long, Set<Long>> likesMap = likeStorage.getUserLikesByFilms(filmIds);
        // Получаем режиссёров для фильмов (как было)
        Map<Long, Set<Director>> directorsMap = this.getFilmDirectors(filmIds);

        return enrichFilms(films, genresMap, likesMap, directorsMap);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void deleteFilm(Long id) {
        // Благодаря ON DELETE CASCADE у всех внешних ключей, явное удаление лайков и жанров не нужно.
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
        Map<Long, Set<Long>> likesMap = likeStorage.getUserLikesByFilms(filmIds);
        Map<Long, Set<Director>> directorMap = getFilmDirectors(filmIds);
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

    private Map<Long, Set<Director>> getFilmDirectors(List<Long> filmIds) {
        // Получаем всех режиссеров для указанных фильмов
        // Запрос возвращает пары (film_id, user_id)
        return jdbc.query(
                        SELECT_DIRECTORS_QUERY,
                        Map.of("filmIds", filmIds),
                        filmDirectorRowMapper
                )
                .stream()

                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toSet())
                ));
    }

    private List<Film> enrichFilms(
            List<Film> films,
            Map<Long, Set<FilmGenre>> genresMap,
            Map<Long, Set<Long>> likesMap,
            Map<Long, Set<Director>> directorMap
    ) {
        return films.stream()
                .map(film -> {
                    Film newFilm = new Film();
                    // Копируем все поля из исходного фильма
                    newFilm.setId(film.getId());
                    newFilm.setName(film.getName());
                    newFilm.setDescription(film.getDescription());
                    newFilm.setReleaseDate(film.getReleaseDate());
                    newFilm.setDuration(film.getDuration());
                    newFilm.setRating(film.getRating());
                    newFilm.setDirectors(directorMap.getOrDefault(film.getId(), new HashSet<>()));

                    // Добавляем дополнительные данные
                    newFilm.setGenres(genresMap.getOrDefault(film.getId(), new HashSet<>()));
                    newFilm.setLikes(likesMap.getOrDefault(film.getId(), new HashSet<>()));
                    return newFilm;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Film> getCommonFilms(Long userId, Long friendId) {
        Map<String, Object> params = Map.of("userId", userId, "friendId", friendId);
        List<Film> films = findMany(SELECT_COMMON_FILMS_WITH_POPULARITY, params);
        if (films == null || films.isEmpty()) {
            return Collections.emptyList();
        }
        return getFilmAdditionalData(films);
    }
}
