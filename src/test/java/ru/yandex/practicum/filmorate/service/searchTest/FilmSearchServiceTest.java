package ru.yandex.practicum.filmorate.service.searchTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.dto.DirectorCreateDTO;
import ru.yandex.practicum.filmorate.dto.DirectorSendDTO;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.mock.MockFilms;
import ru.yandex.practicum.filmorate.enums.FilmGenre;
import ru.yandex.practicum.filmorate.enums.FilmRating;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.yandex.practicum.filmorate.mock.MockUsers.getValidUser;

@SpringBootTest
@ActiveProfiles("db") // Подключаем БД профиль для работы с БД версиями хранилищ
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
// Сбрасывать контекст между методами, чтобы тесты работали изолированно от результатов друг друга.
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmSearchServiceTest {

    private final MockMvc mockMvc;

    private final FilmService filmService;

    private final UserService userService;

    private final DirectorService directorService;

    @Test
    void shouldSearchByTitleSuccessfully() throws Exception {
        // Создаём фильмы
        Film film1 = MockFilms.getValidFilm(1L);
        film1.setName("Inception");
        film1.setGenres(new HashSet<>(Arrays.asList(FilmGenre.ACTION, FilmGenre.THRILLER)));

        Film film2 = MockFilms.getValidFilm(2L);
        film2.setName("Interstellar");
        film2.setGenres(new HashSet<>(List.of(FilmGenre.ACTION)));

        filmService.createFilm(film1);
        filmService.createFilm(film2);

        // Выполняем поиск по названию
        mockMvc.perform(get("/films/search")
                        .param("query", "Ince")
                        .param("by", "title"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Inception"));
    }

    @Test
    void shouldSearchByDirectorSuccessfully() throws Exception {
        // Создаём фильм с режиссёром
        Film film = MockFilms.getValidFilm(1L);
        film.setName("The Dark Knight");
        film.setGenres(new HashSet<>(List.of(FilmGenre.ACTION, FilmGenre.DRAMA)));

        DirectorCreateDTO directorCreateDTO = new DirectorCreateDTO("Christopher Nolan");
        DirectorSendDTO director = directorService.createDirector(directorCreateDTO);
        film.setDirectors(new HashSet<>(List.of(DirectorMapper.mapSendDTOToDomain(director))));

        filmService.createFilm(film);

        // Выполняем поиск по режиссёру
        mockMvc.perform(get("/films/search")
                        .param("query", "Nolan")
                        .param("by", "director"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("The Dark Knight"));
    }

    @Test
    void shouldSearchByBothTitleAndDirectorSuccessfully() throws Exception {
        // Создаём фильм, который должен быть найден по обоим критериям
        Film film = MockFilms.getValidFilm(1L);
        film.setName("Nolan's Masterpiece");

        DirectorCreateDTO directorCreateDTO = new DirectorCreateDTO("Nolan");
        DirectorSendDTO director = directorService.createDirector(directorCreateDTO);
        film.setDirectors(new HashSet<>(List.of(DirectorMapper.mapSendDTOToDomain(director))));

        filmService.createFilm(film);

        // Поиск по обоим критериям через запятую (в любом порядке)
        mockMvc.perform(get("/films/search")
                        .param("query", "Nolan")
                        .param("by", "title,director"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(get("/films/search")
                        .param("query", "Nolan")
                        .param("by", "director,title"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldReturnEmptyListWhenNoMatches() throws Exception {
        // Создаём фильм
        Film film = MockFilms.getValidFilm(1L);
        film.setName("Unique Movie");
        filmService.createFilm(film);

        // Поиск с несуществующим запросом
        mockMvc.perform(get("/films/search")
                        .param("query", "NonExistent")
                        .param("by", "title"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldValidateQueryParameter() throws Exception {
        // Пустой параметр query
        mockMvc.perform(get("/films/search")
                        .param("query", "")
                        .param("by", "title"))
                .andExpect(status().isBadRequest());

        // Отсутствие параметра query
        mockMvc.perform(get("/films/search")
                        .param("by", "title"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldValidateByParameter() throws Exception {
        String validQuery = "test";

        // Некорректное значение параметра by
        mockMvc.perform(get("/films/search")
                        .param("query", validQuery)
                        .param("by", "invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.by").value(containsString("Атрибут by задан неверно")));

        // Пустое значение параметра by
        mockMvc.perform(get("/films/search")
                        .param("query", validQuery)
                        .param("by", ""))
                .andExpect(status().isBadRequest());

        // Отсутствие параметра by
        mockMvc.perform(get("/films/search")
                        .param("query", validQuery))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldHandleCaseInsensitiveSearch() throws Exception {
        // Создаём фильм
        Film film = MockFilms.getValidFilm(1L);
        film.setName("CasE InSeNsItIvE");
        filmService.createFilm(film);

        // Поиск в разных регистрах
        mockMvc.perform(get("/films/search")
                        .param("query", "case")
                        .param("by", "title"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(get("/films/search")
                        .param("query", "INSENSITIVE")
                        .param("by", "title"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldSortByPopularity() throws Exception {
        // Создаём фильмы с разным количеством лайков
        Film popularFilm = MockFilms.getValidFilm(1L);
        popularFilm.setName("Popular Movie");
        Film createdPopular = filmService.createFilm(popularFilm);

        Film unpopularFilm = MockFilms.getValidFilm(2L);
        unpopularFilm.setName("Unpopular Movie");
        Film createdUnpopular = filmService.createFilm(unpopularFilm);

        // Добавляем лайки
        Long user1 = userService.createUser(getValidUser()).getId();
        Long user2 = userService.createUser(getValidUser()).getId();

        filmService.filmAddLike(createdPopular.getId(), user1);
        filmService.filmAddLike(createdPopular.getId(), user2);
        filmService.filmAddLike(createdUnpopular.getId(), user1);

        // Выполняем поиск по названию с сортировкой по популярности (больше лайков = выше в списке)
        mockMvc.perform(get("/films/search")
                        .param("query", "Movie")
                        .param("by", "title"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                // Проверяем, что популярный фильм (2 лайка) идёт первым
                .andExpect(jsonPath("$[0].name").value("Popular Movie"))
                .andExpect(jsonPath("$[0].likes.length()").value(2))
                // А непопулярный (1 лайк) — вторым
                .andExpect(jsonPath("$[1].name").value("Unpopular Movie"))
                .andExpect(jsonPath("$[1].likes.length()").value(1));
    }

    @Test
    void shouldSortByPopularityAndReturnFullFilmData() throws Exception {
        // Создаём режиссёров
        DirectorCreateDTO directorCreateDTO1 = new DirectorCreateDTO("Director One");
        DirectorSendDTO director1 = directorService.createDirector(directorCreateDTO1);

        DirectorCreateDTO directorCreateDTO2 = new DirectorCreateDTO("Director Two");
        DirectorSendDTO director2 = directorService.createDirector(directorCreateDTO2);

        // Создаём фильмы с разным количеством лайков
        Film popularFilm = MockFilms.getValidFilm(1L);
        popularFilm.setName("Popular Movie");
        popularFilm.setGenres(new HashSet<>(Arrays.asList(FilmGenre.COMEDY, FilmGenre.DRAMA)));
        popularFilm.setDirectors(new HashSet<>(List.of(DirectorMapper.mapSendDTOToDomain(director1))));
        popularFilm.setRating(FilmRating.PG_13);

        Film unpopularFilm = MockFilms.getValidFilm(2L);
        unpopularFilm.setName("Unpopular Movie");
        unpopularFilm.setGenres(new HashSet<>(List.of(FilmGenre.ACTION)));
        unpopularFilm.setDirectors(new HashSet<>(List.of(DirectorMapper.mapSendDTOToDomain(director2))));
        unpopularFilm.setRating(FilmRating.R);

        Film createdPopular = filmService.createFilm(popularFilm);
        Film createdUnpopular = filmService.createFilm(unpopularFilm);

        // Добавляем лайки
        Long user1 = userService.createUser(getValidUser()).getId();
        Long user2 = userService.createUser(getValidUser()).getId();

        filmService.filmAddLike(createdPopular.getId(), user1);
        filmService.filmAddLike(createdPopular.getId(), user2);
        filmService.filmAddLike(createdUnpopular.getId(), user1);

        // Выполняем поиск по названию с сортировкой по популярности
        mockMvc.perform(get("/films/search")
                        .param("query", "Movie")
                        .param("by", "title"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                // Проверяем, что популярный фильм (2 лайка) идёт первым
                .andExpect(jsonPath("$[0].name").value("Popular Movie"))
                .andExpect(jsonPath("$[0].likes").isArray())
                .andExpect(jsonPath("$[0].likes").value(hasSize(2)))
                .andExpect(jsonPath("$[0].likes").value(
                        containsInAnyOrder(user1.intValue(), user2.intValue())))
                // Проверяем жанры популярного фильма
                .andExpect(jsonPath("$[0].genres.length()").value(2))
                .andExpect(jsonPath("$[0].genres[*].id").value(hasItems(1, 2))) // COMEDY (1), DRAMA (2)
                .andExpect(jsonPath("$[0].genres[*].name").value(
                        hasItems("Комедия", "Драма")))
                // Проверяем режиссёра популярного фильма
                .andExpect(jsonPath("$[0].directors.length()").value(1))
                .andExpect(jsonPath("$[0].directors[0].id").value(director1.getId()))
//                .andExpect(jsonPath("$[0].directors[0].name").value("Director One"))
                // Проверяем рейтинг популярного фильма
                .andExpect(jsonPath("$[0].mpa.id").value(3)) // PG_13
                .andExpect(jsonPath("$[0].mpa.name").value("PG-13"))
                // Проверяем непопулярный фильм (1 лайк) — идёт вторым
                .andExpect(jsonPath("$[1].name").value("Unpopular Movie"))
                .andExpect(jsonPath("$[1].likes.length()").value(1))
                .andExpect(jsonPath("$[1].likes[0]").value(user1))
                // Проверяем жанры непопулярного фильма
                .andExpect(jsonPath("$[1].genres.length()").value(1))
                .andExpect(jsonPath("$[1].genres[0].id").value(6)) // ACTION (6)
                .andExpect(jsonPath("$[1].genres[0].name").value("Боевик"))
                // Проверяем режиссёра непопулярного фильма
                .andExpect(jsonPath("$[1].directors.length()").value(1))
                .andExpect(jsonPath("$[1].directors[0].id").value(director2.getId()))
//                .andExpect(jsonPath("$[1].directors[0].name").value("Director Two"))
                // Проверяем рейтинг непопулярного фильма
                .andExpect(jsonPath("$[1].mpa.id").value(4)) // R
                .andExpect(jsonPath("$[1].mpa.name").value("R"));
    }

    @Test
    void shouldFindFilmsByTitleAndDirectorSimultaneously() throws Exception {
        // Создаём фильм с искомым текстом в названии
        Film filmByTitle = MockFilms.getValidFilm(1L);
        filmByTitle.setName("Иконокрад");
        filmService.createFilm(filmByTitle);


        // Создаём фильм с искомым текстом в имени режиссёра
        DirectorCreateDTO directorCreateDTO = new DirectorCreateDTO("Крадовец");
        DirectorSendDTO director = directorService.createDirector(directorCreateDTO);


        Film filmByDirector = MockFilms.getValidFilm(2L);
        filmByDirector.setName("Тест");
        filmByDirector.setDirectors(new HashSet<>(List.of(DirectorMapper.mapSendDTOToDomain(director))));
        filmService.createFilm(filmByDirector);


        // Выполняем поиск одновременно по названию и режиссёру с запросом «крад»
        mockMvc.perform(get("/films/search")
                        .param("query", "крад")
                        .param("by", "title,director"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                // Ожидаем 2 фильма: один по названию, один по режиссёру
                .andExpect(jsonPath("$.length()").value(2))
                // Проверяем, что среди результатов есть фильм с названием «Иконокрад»
                .andExpect(jsonPath("$[*].name").value(hasItem("Иконокрад")))
                // Проверяем, что среди результатов есть фильм «Тест»
                .andExpect(jsonPath("$[*].name").value(hasItem("Тест")))
                // Дополнительно проверяем, что первый найденный фильм — «Иконокрад» (порядок может варьироваться)
                // Проверяем, что в ответе есть фильм с названием «Иконокрад»
                .andExpect(jsonPath("$[?(@.name == 'Иконокрад')]").exists())
// Проверяем, что в ответе есть фильм с названием «Тест»
                .andExpect(jsonPath("$[?(@.name == 'Тест')]").exists())
// Дополнительно проверяем длину массива — должно быть ровно 2 фильма
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldNotFindExtraFilms() throws Exception {
        // Создаём фильмы с разными названиями и режиссёрами

        // Фильм, который должен быть найден (содержит «крад» в названии)
        createFilmWithTitleAndDirector("Иконокрад", "Иванов");
        // Фильм, который должен быть найден (содержит «крад» в имени режиссёра)
        createFilmWithTitleAndDirector("Обычный фильм", "Крадовец");
        // Фильмы, которые НЕ должны быть найдены
        createFilmWithTitleAndDirector("Другой фильм", "Петров");
        createFilmWithTitleAndDirector("Ещё один фильм", "Сидоров");
        createFilmWithTitleAndDirector("Без совпадений", "Без совпадений");


        // Выполняем поиск по запросу «крад» одновременно по названию и режиссёру
        mockMvc.perform(get("/films/search")
                        .param("query", "крад")
                        .param("by", "title,director"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                // Ожидаем ровно 2 фильма: «Иконокрад» и фильм с режиссёром «Крадовец»
                .andExpect(jsonPath("$.length()").value(2))
                // Проверяем наличие ожидаемых фильмов
                .andExpect(jsonPath("$[?(@.name == 'Иконокрад')]").exists())
                .andExpect(jsonPath("$[?(@.name == 'Обычный фильм')]").exists())
                // Проверяем отсутствие лишних фильмов
                .andExpect(jsonPath("$[?(@.name == 'Другой фильм')]").doesNotExist())
                .andExpect(jsonPath("$[?(@.name == 'Ещё один фильм')]").doesNotExist())
                .andExpect(jsonPath("$[?(@.name == 'Без совпадений')]").doesNotExist());
    }

    @Test
    void shouldNotReturnFilmsWithoutDirectorsWhenSearchingByDirector() throws Exception {
        // Создаём фильм БЕЗ режиссёра
        Film filmWithoutDirector = MockFilms.getValidFilm(1L);
        filmWithoutDirector.setName("Фильм без режиссёра");
        filmService.createFilm(filmWithoutDirector);

        // Создаём фильм С режиссёром
        DirectorCreateDTO directorCreateDTO = new DirectorCreateDTO("Известный Режиссёр");
        DirectorSendDTO director = directorService.createDirector(directorCreateDTO);

        Film filmWithDirector = MockFilms.getValidFilm(2L);
        filmWithDirector.setName("Фильм с режиссёром");
        filmWithDirector.setDirectors(new HashSet<>(List.of(DirectorMapper.mapSendDTOToDomain(director))));
        filmService.createFilm(filmWithDirector);

        // Выполняем поиск ТОЛЬКО по режиссёру (не по названию)
        mockMvc.perform(get("/films/search")
                        .param("query", "Режиссёр")  // часть имени режиссёра
                        .param("by", "director"))  // ищем только по режиссёру
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                // Ожидаем ровно 1 фильм — только тот, у которого есть режиссёр
                .andExpect(jsonPath("$.length()").value(1))
                // Проверяем, что найденный фильм — именно с режиссёром
                .andExpect(jsonPath("$[0].name").value("Фильм с режиссёром"))
                // Дополнительно проверяем, что фильм без режиссёра НЕ найден
                .andExpect(jsonPath("$[?(@.name == 'Фильм без режиссёра')]").doesNotExist())
                // Убеждаемся, что у найденного фильма есть режиссёр в ответе
                .andExpect(jsonPath("$[0].directors").isArray())
                .andExpect(jsonPath("$[0].directors.length()").value(1));

    }

    @Test
    void shouldNotReturnUnrelatedFilmsWhenSearchingByTitle() throws Exception {
        // Создаём фильм БЕЗ режиссёра (не должен быть найден)
        Film filmWithoutDirector = MockFilms.getValidFilm(1L);
        filmWithoutDirector.setName("Фильм без режиссёра и без ключевых слов");
        filmService.createFilm(filmWithoutDirector);


        // Создаём фильм С режиссёром, который должен быть найден (содержит ключевое слово в названии)
        DirectorCreateDTO directorCreateDTO = new DirectorCreateDTO("Известный Режиссёр");
        DirectorSendDTO director = directorService.createDirector(directorCreateDTO);

        Film filmWithDirector = MockFilms.getValidFilm(2L);
        filmWithDirector.setName("Ищуемый фильм с режиссёром");
        filmWithDirector.setDirectors(new HashSet<>(List.of(DirectorMapper.mapSendDTOToDomain(director))));
        filmService.createFilm(filmWithDirector);

        // Выполняем поиск ТОЛЬКО по названию (не по режиссёру)
        mockMvc.perform(get("/films/search")
                        .param("query", "Ищуемый")  // часть названия искомого фильма
                        .param("by", "title"))  // ищем только по названию
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                // Ожидаем ровно 1 фильм — только тот, что соответствует запросу по названию
                .andExpect(jsonPath("$.length()").value(1))
                // Проверяем, что найденный фильм — именно «Ищуемый фильм с режиссёром»
                .andExpect(jsonPath("$[0].name").value("Ищуемый фильм с режиссёром"))
                // Дополнительно проверяем, что фильм без режиссёра НЕ найден (даже несмотря на отсутствие режиссёра)
                .andExpect(jsonPath("$[?(@.name == 'Фильм без режиссёра и без ключевых слов')]").doesNotExist())
                // Убеждаемся, что у найденного фильма есть режиссёр в ответе (для полноты проверки)
                .andExpect(jsonPath("$[0].directors").isArray())
                .andExpect(jsonPath("$[0].directors.length()").value(1));
    }

    @Test
    void shouldFindFilmsByTitleEvenIfTheyHaveNoDirectorWhenSearchingByTitleAndDirector() throws Exception {
        // Создаём фильм БЕЗ режиссёра, название которого подходит под запрос
        Film filmWithoutDirector = MockFilms.getValidFilm(1L);
        filmWithoutDirector.setName("Ищуемый фильм без режиссёра");
        filmService.createFilm(filmWithoutDirector);


        // Создаём фильм С режиссёром, который должен быть найден по названию
        DirectorCreateDTO directorCreateDTO1 = new DirectorCreateDTO("Обычный Режиссёр");
        DirectorSendDTO director1 = directorService.createDirector(directorCreateDTO1);

        Film filmWithDirectorByTitle = MockFilms.getValidFilm(2L);
        filmWithDirectorByTitle.setName("Ещё один ищуемый фильм");
        filmWithDirectorByTitle.setDirectors(new HashSet<>(List.of(DirectorMapper.mapSendDTOToDomain(director1))));
        filmService.createFilm(filmWithDirectorByTitle);

        // Создаём фильм С режиссёром, который должен быть найден по режиссёру
        DirectorCreateDTO directorCreateDTO2 = new DirectorCreateDTO("Ищуемый Режиссёр");
        DirectorSendDTO director2 = directorService.createDirector(directorCreateDTO2);
        Film filmWithDirectorByDirector = MockFilms.getValidFilm(3L);
        filmWithDirectorByDirector.setName("Фильм с ищуемым режиссёром");
        filmWithDirectorByDirector.setDirectors(new HashSet<>(List.of(DirectorMapper.mapSendDTOToDomain(director2))));
        filmService.createFilm(filmWithDirectorByDirector);

        // Создаём фильм, который НЕ должен быть найден (не подходит ни по названию, ни по режиссёру)
        DirectorCreateDTO directorCreateDTO3 = new DirectorCreateDTO("Другой Режиссёр");
        DirectorSendDTO director3 = directorService.createDirector(directorCreateDTO3);
        Film unrelatedFilm = MockFilms.getValidFilm(4L);
        unrelatedFilm.setName("Неподходящий фильм");
        unrelatedFilm.setDirectors(new HashSet<>(List.of(DirectorMapper.mapSendDTOToDomain(director3))));
        filmService.createFilm(unrelatedFilm);

        // Выполняем поиск одновременно по названию И режиссёру
        mockMvc.perform(get("/films/search")
                        .param("query", "Ищуемый")  // часть названия ИЛИ имени режиссёра
                        .param("by", "title,director"))  // ищем и по названию, и по режиссёру
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                // Ожидаем ровно 3 фильма: два найдены по названию (один с режиссёром, один без), один — по режиссёру
                .andExpect(jsonPath("$.length()").value(3))
                // Проверяем, что среди результатов есть фильм без режиссёра с подходящим названием
                .andExpect(jsonPath("$[?(@.name == 'Ищуемый фильм без режиссёра')]").exists())
                // Проверяем, что среди результатов есть фильм с режиссёром, найденный по названию
                .andExpect(jsonPath("$[?(@.name == 'Ещё один ищуемый фильм')]").exists())
                // Проверяем, что среди результатов есть фильм, найденный по режиссёру
                .andExpect(jsonPath("$[?(@.name == 'Фильм с ищуемым режиссёром')]").exists())
                // Дополнительно проверяем, что фильм, не подходящий ни по одному критерию, НЕ найден
                .andExpect(jsonPath("$[?(@.name == 'Неподходящий фильм')]").doesNotExist())
                // Убеждаемся, что у фильмов с режиссёрами поле directors существует и содержит данные
                .andExpect(jsonPath("$[?(@.name == 'Ещё один ищуемый фильм')].directors").isArray())
                .andExpect(jsonPath("$[?(@.name == 'Ещё один ищуемый фильм')].directors.length()").value(1))
                .andExpect(jsonPath("$[?(@.name == 'Фильм с ищуемым режиссёром')].directors").isArray())
                .andExpect(jsonPath("$[?(@.name == 'Фильм с ищуемым режиссёром')].directors.length()").value(1));
    }


    /**
     * Вспомогательный метод для создания фильма с названием и режиссёром
     */
    private void createFilmWithTitleAndDirector(String title, String directorName) {
        DirectorCreateDTO directorCreateDTO = new DirectorCreateDTO(directorName);
        DirectorSendDTO director = directorService.createDirector(directorCreateDTO);

        Film film = MockFilms.getValidFilm(null);
        film.setName(title);
        film.setDirectors(new HashSet<>(List.of(DirectorMapper.mapSendDTOToDomain(director))));

        filmService.createFilm(film);
    }


}