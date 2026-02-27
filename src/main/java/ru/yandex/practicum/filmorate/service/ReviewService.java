package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dal.ReviewRepository;
import ru.yandex.practicum.filmorate.dto.review.ReviewRequestDto;
import ru.yandex.practicum.filmorate.dto.review.ReviewResponseDto;
import ru.yandex.practicum.filmorate.dto.review.ReviewUpdateDto;
import ru.yandex.practicum.filmorate.enums.EventOperation;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewRatingService reviewRatingService;
    private final UserService userService;
    private final FilmService filmService;
    private final FeedService feedService;

    @Transactional
    public ReviewResponseDto create(ReviewRequestDto dto) {
        log.info("Создание отзыва пользователем с id=\"{}\" к фильму с id=\"{}\"", dto.userId(), dto.filmId());

        userService.checkThatUserExists(dto.userId());
        filmService.checkThatFilmExists(dto.filmId());

        var review = ReviewMapper.mapToReview(dto);
        review = reviewRepository.create(review);

        feedService.logEvent(dto.userId(), EventType.REVIEW, EventOperation.ADD, review.getId());

        return ReviewMapper.mapToResponseDto(review, reviewRatingService.getUseful(review.getId()));
    }

    @Transactional
    public ReviewResponseDto update(ReviewUpdateDto dto) {
        log.info("Обновление отзыва с id=\"{}\"", dto.reviewId());

        var review = reviewRepository.updateById(dto.reviewId(), dto.content(), dto.isPositive())
                .orElseThrow(() -> new NotFoundException("Отзыв с id=" + dto.reviewId() + " не найден"));
        var useful = reviewRatingService.getUseful(dto.reviewId());

        feedService.logEvent(review.getUserId(), EventType.REVIEW, EventOperation.UPDATE, review.getId());

        return ReviewMapper.mapToResponseDto(review, useful);
    }

    public void delete(Long reviewId) {
        log.info("Удаление отзыва с id=\"{}\"", reviewId);
        var review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Отзыв с id=" + reviewId + " не найден"));

        reviewRepository.deleteById(reviewId);
        feedService.logEvent(review.getUserId(), EventType.REVIEW, EventOperation.REMOVE, reviewId);
    }

    public ReviewResponseDto getById(Long reviewId) {
        log.info("Получение отзыва с id=\"{}\"", reviewId);

        var review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Отзыв с id=" + reviewId + " не найден"));
        var useful = reviewRatingService.getUseful(reviewId);
        return ReviewMapper.mapToResponseDto(review, useful);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getReviews(Long filmId, int count) {
        log.info("Получение отзывов для фильма с id=\"{}\" в количестве {}", filmId, count);

        var reviews = filmId == null
                ? reviewRepository.findAll(count)
                : reviewRepository.findByFilmId(filmId, count);

        List<Long> reviewIds = reviews.stream().map(Review::getId).toList();
        Map<Long, Integer> usefulMap = reviewRatingService.getUsefulByReviewIds(reviewIds);

        return reviews.stream()
                .map(review -> ReviewMapper.mapToResponseDto(review,
                        usefulMap.getOrDefault(review.getId(), 0)))
                .sorted(Comparator.comparing(ReviewResponseDto::reviewId))
                .sorted(Comparator.comparing(ReviewResponseDto::useful).reversed())
                .toList();
    }
}
