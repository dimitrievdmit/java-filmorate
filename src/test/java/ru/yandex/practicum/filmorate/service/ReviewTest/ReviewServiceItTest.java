package ru.yandex.practicum.filmorate.service.ReviewTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dto.review.ReviewRequestDto;
import ru.yandex.practicum.filmorate.dto.review.ReviewResponseDto;
import ru.yandex.practicum.filmorate.dto.review.ReviewUpdateDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("db")
@AutoConfigureTestDatabase
@Sql(scripts = "/test-data-reviews.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ReviewServiceItTest {

    @Autowired
    private ReviewService reviewService;

    @Test
    void testCreate() {
        ReviewRequestDto dto = new ReviewRequestDto(2L, 2L, "New content", true);

        ReviewResponseDto result = reviewService.create(dto);

        assertThat(result.reviewId()).isNotNull();
        assertThat(result.content()).isEqualTo("New content");
        assertThat(result.useful()).isEqualTo(0);
    }

    @Test
    void testGetById() {
        ReviewResponseDto result = reviewService.getById(1L);

        assertThat(result.reviewId()).isEqualTo(1L);
        assertThat(result.content()).isEqualTo("Content1");
        assertThat(result.isPositive()).isTrue();
    }

    @Test
    void testGetByIdNotFound() {
        assertThrows(NotFoundException.class, () -> reviewService.getById(999L));
    }

    @Test
    void testGetByIdUseful() {
        ReviewResponseDto result = reviewService.getById(1L);

        assertThat(result.useful()).isEqualTo(1);
    }

    @Test
    void testUpdate() {
        ReviewUpdateDto dto = new ReviewUpdateDto(2L, "Updated content", true);

        ReviewResponseDto result = reviewService.update(dto);

        assertThat(result.reviewId()).isEqualTo(2L);
        assertThat(result.content()).isEqualTo("Updated content");
        assertThat(result.isPositive()).isTrue();
    }

    @Test
    void testUpdateNotFound() {
        ReviewUpdateDto dto = new ReviewUpdateDto(999L, "Content", true);

        assertThrows(NotFoundException.class, () -> reviewService.update(dto));
    }

    @Test
    void testDelete() {
        reviewService.delete(3L);

        assertThrows(NotFoundException.class, () -> reviewService.getById(3L));
    }

    @Test
    void testGetReviewsByFilmId() {
        List<ReviewResponseDto> result = reviewService.getReviews(1L, 10);

        assertThat(result).hasSize(3)
                .extracting(ReviewResponseDto::filmId)
                .containsOnly(1L);
    }

    @Test
    void testGetReviewsByFilmIdEmpty() {
        List<ReviewResponseDto> result = reviewService.getReviews(999L, 10);

        assertThat(result).isEmpty();
    }

    @Test
    void testGetAllReviews() {
        List<ReviewResponseDto> result = reviewService.getReviews(null, 10);

        assertThat(result).hasSize(6);
    }

    @Test
    void testGetReviewsWithCount() {
        List<ReviewResponseDto> result = reviewService.getReviews(null, 1);

        assertThat(result).hasSize(1);
    }
}