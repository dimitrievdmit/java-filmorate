package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.review.ReviewRequestDto;
import ru.yandex.practicum.filmorate.dto.review.ReviewResponseDto;
import ru.yandex.practicum.filmorate.model.Review;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReviewMapper {

    public static Review mapToReview(ReviewRequestDto dto) {
        Review review = new Review();
        review.setUserId(dto.userId());
        review.setFilmId(dto.filmId());
        review.setContent(dto.content());
        review.setPositive(dto.isPositive());
        return review;
    }

    public static ReviewResponseDto mapToResponseDto(Review review, Integer useful) {
        return new ReviewResponseDto(
                review.getId(),
                review.getContent(),
                review.getPositive(),
                review.getUserId(),
                review.getFilmId(),
                useful
        );
    }
}