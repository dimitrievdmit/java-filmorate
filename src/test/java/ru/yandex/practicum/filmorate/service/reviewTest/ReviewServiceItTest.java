package ru.yandex.practicum.filmorate.service.reviewTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dto.review.ReviewRequestDto;
import ru.yandex.practicum.filmorate.dto.review.ReviewResponseDto;
import ru.yandex.practicum.filmorate.dto.review.ReviewUpdateDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mock.MockFilms;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.yandex.practicum.filmorate.mock.MockUsers.getValidUser;

@SpringBootTest
@ActiveProfiles("db")
@AutoConfigureTestDatabase
// Сбрасывать контекст между методами, чтобы тесты работали изолированно от результатов друг друга.
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ReviewServiceItTest {

    private final ReviewService reviewService;
    private final FilmService filmService;
    private final UserService userService;

    @Test
    void testCreate() {
        // Создаём фильм
        Film film = filmService.createFilm(MockFilms.getValidFilm());
        // Создаём пользователя
        Long userId = userService.createUser(getValidUser()).getId();

        ReviewRequestDto dto = new ReviewRequestDto(userId, film.getId(), "New content", true);

        ReviewResponseDto result = reviewService.create(dto);

        assertThat(result.reviewId()).isNotNull();
        assertThat(result.content()).isEqualTo("New content");
        assertThat(result.useful()).isEqualTo(0);
    }

    @Test
    @Sql(scripts = "/test-data-reviews.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testGetById() {
        ReviewResponseDto result = reviewService.getById(1L);

        assertThat(result.reviewId()).isEqualTo(1L);
        assertThat(result.content()).isEqualTo("Content1");
        assertThat(result.isPositive()).isTrue();
    }

    @Test
    @Sql(scripts = "/test-data-reviews.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testGetByIdNotFound() {
        assertThrows(NotFoundException.class, () -> reviewService.getById(999L));
    }

    @Test
    @Sql(scripts = "/test-data-reviews.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testGetByIdUseful() {
        ReviewResponseDto result = reviewService.getById(1L);

        assertThat(result.useful()).isEqualTo(1);
    }

    @Test
    @Sql(scripts = "/test-data-reviews.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testUpdate() {
        ReviewUpdateDto dto = new ReviewUpdateDto(2L, "Updated content", true);

        ReviewResponseDto result = reviewService.update(dto);

        assertThat(result.reviewId()).isEqualTo(2L);
        assertThat(result.content()).isEqualTo("Updated content");
        assertThat(result.isPositive()).isTrue();
    }

    @Test
    @Sql(scripts = "/test-data-reviews.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testUpdateNotFound() {
        ReviewUpdateDto dto = new ReviewUpdateDto(999L, "Content", true);

        assertThrows(NotFoundException.class, () -> reviewService.update(dto));
    }

    @Test
    @Sql(scripts = "/test-data-reviews.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testDelete() {
        reviewService.delete(3L);

        assertThrows(NotFoundException.class, () -> reviewService.getById(3L));
    }

    @Test
    @Sql(scripts = "/test-data-reviews.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testGetReviewsByFilmId() {
        List<ReviewResponseDto> result = reviewService.getReviews(1L, 10);

        assertThat(result).hasSize(3)
                .extracting(ReviewResponseDto::filmId)
                .containsOnly(1L);
    }

    @Test
    @Sql(scripts = "/test-data-reviews.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testGetReviewsByFilmIdEmpty() {
        List<ReviewResponseDto> result = reviewService.getReviews(999L, 10);

        assertThat(result).isEmpty();
    }

    @Test
    @Sql(scripts = "/test-data-reviews.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testGetAllReviews() {
        List<ReviewResponseDto> result = reviewService.getReviews(null, 10);

        assertThat(result).hasSize(6);
    }

    @Test
    @Sql(scripts = "/test-data-reviews.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void testGetReviewsWithCount() {
        List<ReviewResponseDto> result = reviewService.getReviews(null, 1);

        assertThat(result).hasSize(1);
    }
}