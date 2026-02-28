package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.mock.MockFilms;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.filmorate.mock.MockUsers.getValidUser;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RecommendationServiceTest {

    private final RecommendationService recommendationService;
    private final FilmService filmService;
    private final UserService userService;

    @Test
    void shouldReturnEmptyListWhenUserHasNoLikes() {
        // Создаём пользователя без лайков
        Long userId = userService.createUser(getValidUser()).getId();

        // Получаем рекомендации
        Collection<Film> recommendations = recommendationService.getRecommendedFilms(userId, 10L);

        // Проверяем, что список пуст
        assertTrue(recommendations.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenNoCommonLikesWithOtherUsers() {
        // Создаём двух пользователей
        Long user1Id = userService.createUser(getValidUser()).getId();
        Long user2Id = userService.createUser(getValidUser()).getId();

        // Создаём фильмы
        Film film1 = filmService.createFilm(MockFilms.getValidFilm(1L));
        Film film2 = filmService.createFilm(MockFilms.getValidFilm(2L));

        // Пользователь 1 лайкает фильм 1
        filmService.filmAddLike(film1.getId(), user1Id);
        // Пользователь 2 лайкает фильм 2
        filmService.filmAddLike(film2.getId(), user2Id);

        // Получаем рекомендации для пользователя 1
        List<Film> recommendations = (List<Film>) recommendationService.getRecommendedFilms(user1Id, 10L);

        // Проверяем, что список пуст (нет общих лайков)
        assertTrue(recommendations.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenSameLikesAndNoOtherFilms() {
        // Создаём двух пользователей
        Long user1Id = userService.createUser(getValidUser()).getId();
        Long user2Id = userService.createUser(getValidUser()).getId();

        // Создаём один фильм
        Film film = filmService.createFilm(MockFilms.getValidFilm(1L));

        // Оба пользователя лайкают один и тот же фильм
        filmService.filmAddLike(film.getId(), user1Id);
        filmService.filmAddLike(film.getId(), user2Id);

        // Получаем рекомендации для пользователя 1
        List<Film> recommendations = (List<Film>) recommendationService.getRecommendedFilms(user1Id, 10L);

        // Проверяем, что список пуст (одинаковые лайки, нет других фильмов)
        assertTrue(recommendations.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenUserWithMoreLikesRequestsRecommendations() {
        // Создаём двух пользователей
        Long user1Id = userService.createUser(getValidUser()).getId();
        Long user2Id = userService.createUser(getValidUser()).getId();

        // Создаём два фильма
        Film film1 = filmService.createFilm(MockFilms.getValidFilm(1L));
        Film film2 = filmService.createFilm(MockFilms.getValidFilm(2L));

        // Пользователь 1 лайкает фильм 1
        filmService.filmAddLike(film1.getId(), user1Id);
        // Пользователь 2 лайкает оба фильма
        filmService.filmAddLike(film1.getId(), user2Id);
        filmService.filmAddLike(film2.getId(), user2Id);

        // Получаем рекомендации для пользователя 2 (у которого больше лайков)
        List<Film> recommendations = (List<Film>) recommendationService.getRecommendedFilms(user2Id, 10L);

        // Проверяем, что список пуст (пользователь 2 уже лайкнул все фильмы похожих пользователей)
        assertTrue(recommendations.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenSameLikesOnTwoFilms() {
        // Создаём двух пользователей
        Long user1Id = userService.createUser(getValidUser()).getId();
        Long user2Id = userService.createUser(getValidUser()).getId();

        // Создаём два фильма
        Film film1 = filmService.createFilm(MockFilms.getValidFilm(1L));
        Film film2 = filmService.createFilm(MockFilms.getValidFilm(2L));

        // Оба пользователя лайкают оба фильма
        filmService.filmAddLike(film1.getId(), user1Id);
        filmService.filmAddLike(film2.getId(), user1Id);
        filmService.filmAddLike(film1.getId(), user2Id);
        filmService.filmAddLike(film2.getId(), user2Id);

        // Получаем рекомендации для пользователя 1
        List<Film> recommendations = (List<Film>) recommendationService.getRecommendedFilms(user1Id, 10L);

        // Проверяем, что список пуст (одинаковые лайки на два фильма)
        assertTrue(recommendations.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenCountIsZero() {
        // Создаём пользователя и добавляем ему лайк
        Long userId = userService.createUser(getValidUser()).getId();
        Film film1 = filmService.createFilm(MockFilms.getValidFilm(1L));
        filmService.filmAddLike(film1.getId(), userId);

        // Создаём похожего пользователя с общим фильмом и дополнительным
        Long similarUserId = userService.createUser(getValidUser()).getId();
        Film film2 = filmService.createFilm(MockFilms.getValidFilm(2L));
        filmService.filmAddLike(film1.getId(), similarUserId); // общий фильм
        filmService.filmAddLike(film2.getId(), similarUserId); // дополнительный фильм

        // Запрашиваем 0 рекомендаций
        List<Film> recommendations = (List<Film>) recommendationService.getRecommendedFilms(userId, 0L);

        // Проверяем, что возвращается пустой список
        assertTrue(recommendations.isEmpty(), "При count = 0 должен возвращаться пустой список");
    }

    @Test
    void shouldReturnEmptyListWhenNoRecommendationsAvailableAndCountIsLarge() {
        // Сценарий: у пользователя есть лайки, но нет возможных рекомендаций
        // (все похожие пользователи лайкнули те же фильмы)

        Long userId = userService.createUser(getValidUser()).getId();
        Film sharedFilm1 = filmService.createFilm(MockFilms.getValidFilm(1L));
        Film sharedFilm2 = filmService.createFilm(MockFilms.getValidFilm(2L));


        // Целевой пользователь лайкает два фильма
        filmService.filmAddLike(sharedFilm1.getId(), userId);
        filmService.filmAddLike(sharedFilm2.getId(), userId);

        // Похожий пользователь лайкает те же два фильма (нет новых фильмов для рекомендации)
        Long similarUserId = userService.createUser(getValidUser()).getId();
        filmService.filmAddLike(sharedFilm1.getId(), similarUserId);
        filmService.filmAddLike(sharedFilm2.getId(), similarUserId);

        // Запрашиваем большое количество рекомендаций (100)
        List<Film> recommendations = (List<Film>) recommendationService.getRecommendedFilms(userId, 100L);

        // Проверяем, что даже при большом count возвращается пустой список,
        // если нет доступных рекомендаций
        assertTrue(recommendations.isEmpty(),
                "Должен возвращать пустой список, когда нет возможных рекомендаций, независимо от значения count");
    }

    @Test
    void shouldReturnOneFilmWhenPartialIntersection() {
        // Создаём двух пользователей
        Long user1Id = userService.createUser(getValidUser()).getId();
        Long user2Id = userService.createUser(getValidUser()).getId();

        // Создаём два фильма
        Film film1 = MockFilms.getValidFilm(1L);
        Film film2 = MockFilms.getValidFilm(2L);

        film1.setName("Film 1");
        film2.setName("Film 2");

        film1 = filmService.createFilm(film1);
        film2 = filmService.createFilm(film2);

        // Пользователь 1 лайкает только фильм 1
        filmService.filmAddLike(film1.getId(), user1Id);
        // Пользователь 2 лайкает оба фильма
        filmService.filmAddLike(film1.getId(), user2Id);
        filmService.filmAddLike(film2.getId(), user2Id);

        // Получаем рекомендации для пользователя 1
        List<Film> recommendations = (List<Film>) recommendationService.getRecommendedFilms(user1Id, 10L);

        // Проверяем результаты
        assertEquals(1, recommendations.size());
        assertEquals(film2.getId(), recommendations.getFirst().getId());
        assertEquals("Film 2", recommendations.getFirst().getName());
    }

    @Test
    void shouldReturnTopNRecommendationsWhenMultipleOptionsAvailable() {
        // Создаём трёх пользователей
        Long user1Id = userService.createUser(getValidUser()).getId();
        Long user2Id = userService.createUser(getValidUser()).getId();
        Long user3Id = userService.createUser(getValidUser()).getId();

        // Создаём четыре фильма
        Film film1 = MockFilms.getValidFilm(1L);
        Film film2 = MockFilms.getValidFilm(2L);
        Film film3 = MockFilms.getValidFilm(3L);
        Film film4 = MockFilms.getValidFilm(4L);

        film1.setName("Popular Film");
        film2.setName("Less Popular Film 1");
        film3.setName("Less Popular Film 2");
        film4.setName("Rare Film");

        film1 = filmService.createFilm(film1);
        film2 = filmService.createFilm(film2);
        film3 = filmService.createFilm(film3);
        film4 = filmService.createFilm(film4);

        // Пользователь 1 лайкает только film1
        filmService.filmAddLike(film1.getId(), user1Id);

        // Пользователи 2 и 3 лайкают film1 и film4 (film4 должен быть рекомендован с частотой 2)
        filmService.filmAddLike(film1.getId(), user2Id);
        filmService.filmAddLike(film4.getId(), user2Id);
        filmService.filmAddLike(film1.getId(), user3Id);
        filmService.filmAddLike(film4.getId(), user3Id);

        // Пользователь 2 также лайкает film2, а пользователь 3 — film3 (эти фильмы должны быть рекомендованы с частотой 1)
        filmService.filmAddLike(film2.getId(), user2Id);
        filmService.filmAddLike(film3.getId(), user3Id);

        // Получаем топ‑2 рекомендации для пользователя 1
        List<Film> recommendations = (List<Film>) recommendationService.getRecommendedFilms(user1Id, 2L);

        // Проверяем результаты
        assertEquals(2, recommendations.size(), "Должно быть возвращено ровно 2 рекомендации");

        // film4 должен быть первым (частота 2), так как его лайкнули оба похожих пользователя
        assertEquals(film4.getId(), recommendations.get(0).getId(),
                "Первый в списке должен быть фильм с наибольшей частотой упоминания (film4)");
        assertEquals("Rare Film", recommendations.get(0).getName());

        // Второй фильм может быть либо film2, либо film3 (оба с частотой 1), порядок между ними не гарантирован
        Long secondFilmId = recommendations.get(1).getId();
        assertTrue(secondFilmId.equals(film2.getId()) || secondFilmId.equals(film3.getId()),
                "Второй фильм должен быть либо film2, либо film3, так как оба упомянуты один раз");
    }

    @Test
    void shouldHandleMultipleSimilarUsersWithDifferentSimilarityScores() {
        // Создаём четырёх пользователей
        Long targetUserId = userService.createUser(getValidUser()).getId();
        Long similarUser1Id = userService.createUser(getValidUser()).getId(); // высокая схожесть
        Long similarUser2Id = userService.createUser(getValidUser()).getId(); // средняя схожесть
        Long similarUser3Id = userService.createUser(getValidUser()).getId(); // низкая схожесть

        // Создаём фильмы
        Film commonFilm1 = MockFilms.getValidFilm(1L);
        Film commonFilm2 = MockFilms.getValidFilm(2L);
        Film commonFilm3 = MockFilms.getValidFilm(3L);
        Film uniqueToUser1 = MockFilms.getValidFilm(4L);
        Film uniqueToUser2 = MockFilms.getValidFilm(5L);
        Film uniqueToUser3 = MockFilms.getValidFilm(6L);

        commonFilm1.setName("Common Film 1");
        commonFilm2.setName("Common Film 2");
        commonFilm3.setName("Common Film 3");
        uniqueToUser1.setName("Unique to User 1");
        uniqueToUser2.setName("Unique to User 2");
        uniqueToUser3.setName("Unique to User 3");

        commonFilm1 = filmService.createFilm(commonFilm1);
        commonFilm2 = filmService.createFilm(commonFilm2);
        commonFilm3 = filmService.createFilm(commonFilm3);
        uniqueToUser1 = filmService.createFilm(uniqueToUser1);
        uniqueToUser2 = filmService.createFilm(uniqueToUser2);
        uniqueToUser3 = filmService.createFilm(uniqueToUser3);

        // Целевой пользователь лайкает три общих фильма
        filmService.filmAddLike(commonFilm1.getId(), targetUserId);
        filmService.filmAddLike(commonFilm2.getId(), targetUserId);
        filmService.filmAddLike(commonFilm3.getId(), targetUserId);

        // similarUser1: все три общих + уникальный (схожесть = 3)
        filmService.filmAddLike(commonFilm1.getId(), similarUser1Id);
        filmService.filmAddLike(commonFilm2.getId(), similarUser1Id);
        filmService.filmAddLike(commonFilm3.getId(), similarUser1Id);
        filmService.filmAddLike(uniqueToUser1.getId(), similarUser1Id);


        // similarUser2: два общих + уникальный (схожесть = 2)
        filmService.filmAddLike(commonFilm1.getId(), similarUser2Id);
        filmService.filmAddLike(commonFilm2.getId(), similarUser2Id);
        filmService.filmAddLike(uniqueToUser2.getId(), similarUser2Id);

        // similarUser3: один общий + уникальный (схожесть = 1)
        filmService.filmAddLike(commonFilm1.getId(), similarUser3Id);
        filmService.filmAddLike(uniqueToUser3.getId(), similarUser3Id);

        // Получаем рекомендации (топ‑3) для целевого пользователя
        List<Film> recommendations = (List<Film>) recommendationService.getRecommendedFilms(targetUserId, 3L);

        // Проверяем результаты
        assertEquals(3, recommendations.size(), "Должно быть возвращено 3 рекомендации");

        // Приоритет: фильмы от пользователей с большей схожестью
        // uniqueToUser1 (от similarUser1, схожесть 3) должен быть первым
        // uniqueToUser2 (от similarUser2, схожесть 2) — вторым
        // uniqueToUser3 (от similarUser3, схожесть 1) — третьим
        assertEquals(uniqueToUser1.getId(), recommendations.get(0).getId(),
                "Первым должен быть фильм от пользователя с наибольшей схожестью (similarUser1)");
        assertEquals(uniqueToUser2.getId(), recommendations.get(1).getId(),
                "Вторым должен быть фильм от пользователя со средней схожестью (similarUser2)");
        assertEquals(uniqueToUser3.getId(), recommendations.get(2).getId(),
                "Третьим должен быть фильм от пользователя с наименьшей схожестью (similarUser3)");
    }

    @Test
    void shouldReturnAllAvailableRecommendationsWhenCountExceedsAvailable() {
        // Создаём пользователя с одним лайком
        Long userId = userService.createUser(getValidUser()).getId();

        Film commonFilm = MockFilms.getValidFilm(1L);
        commonFilm.setName("Common Film");
        commonFilm = filmService.createFilm(commonFilm);


        filmService.filmAddLike(commonFilm.getId(), userId);

        // Создаём похожего пользователя с общим и одним дополнительным фильмом
        Long similarUserId = userService.createUser(getValidUser()).getId();

        Film uniqueFilm = MockFilms.getValidFilm(2L);
        uniqueFilm.setName("Unique Film");
        uniqueFilm = filmService.createFilm(uniqueFilm);

        filmService.filmAddLike(commonFilm.getId(), similarUserId);
        filmService.filmAddLike(uniqueFilm.getId(), similarUserId);

        // Также создаём второго похожего пользователя, который лайкает тот же дополнительный фильм
        Long anotherSimilarUserId = userService.createUser(getValidUser()).getId();
        filmService.filmAddLike(commonFilm.getId(), anotherSimilarUserId);
        filmService.filmAddLike(uniqueFilm.getId(), anotherSimilarUserId);

        // Запрашиваем 10 рекомендаций, хотя доступна только 1 уникальная
        List<Film> recommendations = (List<Film>) recommendationService.getRecommendedFilms(userId, 10L);

        // Проверяем результаты
        assertEquals(1, recommendations.size(),
                "Должен вернуть все доступные рекомендации (1), даже если запрошено больше (10)");

        assertEquals(uniqueFilm.getId(), recommendations.getFirst().getId(),
                "Единственная рекомендация должна быть uniqueFilm");

        assertEquals("Unique Film", recommendations.getFirst().getName(),
                "Название фильма в рекомендации должно соответствовать созданному");
    }

    @Test
    void shouldHandleNegativeCountGracefully() {
        // Создаём пользователя с лайками
        Long userId = userService.createUser(getValidUser()).getId();

        Film film1 = MockFilms.getValidFilm(1L);
        Film film2 = MockFilms.getValidFilm(2L);
        film1.setName("Film 1");
        film2.setName("Film 2");
        film1 = filmService.createFilm(film1);
        film2 = filmService.createFilm(film2);

        filmService.filmAddLike(film1.getId(), userId);
        filmService.filmAddLike(film2.getId(), userId);

        // Создаём похожих пользователей с дополнительными фильмами
        Long similarUser1Id = userService.createUser(getValidUser()).getId();
        Long similarUser2Id = userService.createUser(getValidUser()).getId();

        Film recommendedFilm1 = MockFilms.getValidFilm(3L);
        Film recommendedFilm2 = MockFilms.getValidFilm(4L);
        recommendedFilm1.setName("Recommended 1");
        recommendedFilm2.setName("Recommended 2");
        recommendedFilm1 = filmService.createFilm(recommendedFilm1);
        recommendedFilm2 = filmService.createFilm(recommendedFilm2);

        filmService.filmAddLike(film1.getId(), similarUser1Id);
        filmService.filmAddLike(recommendedFilm1.getId(), similarUser1Id);

        filmService.filmAddLike(film2.getId(), similarUser2Id);
        filmService.filmAddLike(recommendedFilm2.getId(), similarUser2Id);

        // Запрашиваем отрицательное количество рекомендаций
        List<Film> recommendations = (List<Film>) recommendationService.getRecommendedFilms(userId, -5L);

        // Проверяем поведение сервиса при отрицательном count
        assertTrue(recommendations.isEmpty(),
                "При отрицательном count должен возвращаться пустой список — это безопасная обработка некорректного ввода");

        // Дополнительная проверка: убеждаемся, что сервис корректно обрабатывает другие отрицательные значения
        List<Film> recommendations2 = (List<Film>) recommendationService.getRecommendedFilms(userId, -100L);
        assertTrue(recommendations2.isEmpty(),
                "Сервис должен возвращать пустой список для любого отрицательного значения count");
    }

    @Test
    void shouldRespectCountLimitWhenMultipleHighFrequencyFilms() {
        // Создаём целевого пользователя с одним фильмом
        Long targetUserId = userService.createUser(getValidUser()).getId();

        Film targetFilm = MockFilms.getValidFilm(1L);
        targetFilm = filmService.createFilm(targetFilm);
        filmService.filmAddLike(targetFilm.getId(), targetUserId);

        // Создаём трёх похожих пользователей, все лайкают один и тот же дополнительный фильм
        Long user1Id = userService.createUser(getValidUser()).getId();
        Long user2Id = userService.createUser(getValidUser()).getId();
        Long user3Id = userService.createUser(getValidUser()).getId();

        Film popularRecommendation = MockFilms.getValidFilm(2L);
        popularRecommendation.setName("Popular Recommendation");
        popularRecommendation = filmService.createFilm(popularRecommendation);

        // Все три похожих пользователя лайкают целевой фильм и популярный рекомендуемый
        filmService.filmAddLike(targetFilm.getId(), user1Id);
        filmService.filmAddLike(popularRecommendation.getId(), user1Id);

        filmService.filmAddLike(targetFilm.getId(), user2Id);
        filmService.filmAddLike(popularRecommendation.getId(), user2Id);

        filmService.filmAddLike(targetFilm.getId(), user3Id);
        filmService.filmAddLike(popularRecommendation.getId(), user3Id);

        // Также создаём ещё один фильм, который лайкнули два похожих пользователя
        Film lessPopularFilm = MockFilms.getValidFilm(3L);
        lessPopularFilm.setName("Less Popular Film");
        lessPopularFilm = filmService.createFilm(lessPopularFilm);

        filmService.filmAddLike(lessPopularFilm.getId(), user1Id);
        filmService.filmAddLike(lessPopularFilm.getId(), user2Id);

        // Запрашиваем только 1 рекомендацию
        List<Film> recommendations = (List<Film>) recommendationService.getRecommendedFilms(targetUserId, 1L);

        // Проверяем, что ограничение count работает корректно
        assertEquals(1, recommendations.size(),
                "Должно быть возвращено ровно 1 рекомендация, несмотря на наличие нескольких вариантов");

        assertEquals(popularRecommendation.getId(), recommendations.getFirst().getId(),
                "Единственной рекомендацией должен быть фильм с наибольшей частотой упоминания (3)");
        assertEquals("Popular Recommendation", recommendations.getFirst().getName(),
                "Название рекомендованного фильма должно соответствовать созданному");

        // Тестируем с count = 2, чтобы убедиться, что порядок рекомендаций правильный
        List<Film> twoRecommendations = (List<Film>) recommendationService.getRecommendedFilms(targetUserId, 2L);

        assertEquals(2, twoRecommendations.size(),
                "При запросе 2 рекомендаций должно быть возвращено 2 фильма");

        // Популярный фильм должен быть первым
        assertEquals(popularRecommendation.getId(), twoRecommendations.get(0).getId(),
                "Первым в списке должен быть фильм с наибольшей частотой (3)");

        // Менее популярный фильм должен быть вторым
        assertEquals(lessPopularFilm.getId(), twoRecommendations.get(1).getId(),
                "Вторым в списке должен быть фильм со средней частотой (2)");
    }

}