package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dal.ReviewRatingRepository;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;

import java.util.List;
import java.util.Map;

@Service
@Profile("db")
@AllArgsConstructor
@Slf4j
public class ReviewRatingService {

    private final ReviewRatingRepository reviewRatingRepository;

    @Transactional
    public void addLike(Long reviewId, Long userId) {
        log.info("Пользователь c id=\"{}\" ставит лайк отзыву с id=\"{}\"", userId, reviewId);

        if (existsLike(reviewId, userId)) throw new AlreadyExistException("Вы уже поставили свою оценку");

        reviewRatingRepository.addLike(reviewId, userId);
    }

    @Transactional
    public void addDislike(Long reviewId, Long userId) {
        log.info("Пользователь c id=\"{}\" ставит дизлайк отзыву с id=\"{}\"", userId, reviewId);

        if (existsLike(reviewId, userId)) {
            reviewRatingRepository.removeVote(reviewId, userId);
        }

        reviewRatingRepository.addDislike(reviewId, userId);
    }

    public void removeVote(Long reviewId, Long userId) {
        log.info("Пользователь c id=\"{}\" удаляет лайк отзыву с id=\"{}\"", userId, reviewId);

        reviewRatingRepository.removeVote(reviewId, userId);
    }

    public Integer getUseful(Long reviewId) {
        log.info("Отображение рейтинга для отзыва с id=\"{}\"", reviewId);

        return reviewRatingRepository.getUseful(reviewId);
    }

    public Map<Long, Integer> getUsefulByReviewIds(List<Long> reviewIds) {
        return reviewRatingRepository.getUsefulByReviewIds(reviewIds);
    }

    private Boolean existsLike(Long reviewId, Long userId) {
        log.info("Проверка на существующий лайк/дизлайк пользователем с id=\"{}\" отзыву с id=\"{}\"",
                userId, reviewId);

        return reviewRatingRepository.exists(reviewId, userId);
    }
}
