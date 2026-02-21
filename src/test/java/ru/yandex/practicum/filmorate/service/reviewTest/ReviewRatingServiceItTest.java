package ru.yandex.practicum.filmorate.service.reviewTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.service.ReviewRatingService;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("db")
@AutoConfigureTestDatabase
@Sql(scripts = "/test-data-reviews.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ReviewRatingServiceItTest {

    @Autowired
    private ReviewRatingService reviewRatingService;

    @Test
    void testGetUseful() {
        Integer useful = reviewRatingService.getUseful(1L);

        assertThat(useful).isEqualTo(1);
    }

    @Test
    void testGetUsefulNoVotes() {
        Integer useful = reviewRatingService.getUseful(2L);

        assertThat(useful).isEqualTo(0);
    }

    @Test
    void testAddLike() {
        reviewRatingService.addLike(4L, 1L);

        assertThat(reviewRatingService.getUseful(4L)).isEqualTo(1);
    }

    @Test
    void testAddDislike() {
        reviewRatingService.addDislike(5L, 2L);

        assertThat(reviewRatingService.getUseful(5L)).isEqualTo(-1);
    }

    @Test
    void testAddLikeDuplicate() {
        assertThrows(AlreadyExistException.class,
                () -> reviewRatingService.addLike(1L, 2L));
    }

    @Test
    void testRemoveVote() {
        reviewRatingService.removeVote(6L, 1L);

        assertThat(reviewRatingService.getUseful(6L)).isEqualTo(0);
    }

    @Test
    void testGetUsefulByReviewIds() {
        Map<Long, Integer> result = reviewRatingService.getUsefulByReviewIds(List.of(1L, 2L, 3L));

        assertThat(result).containsEntry(1L, 1);
        assertThat(result).doesNotContainKey(2L);
        assertThat(result).doesNotContainKey(3L);
    }

    @Test
    void testGetUsefulByReviewIdsEmpty() {
        Map<Long, Integer> result = reviewRatingService.getUsefulByReviewIds(List.of());

        assertThat(result).isEmpty();
    }
}