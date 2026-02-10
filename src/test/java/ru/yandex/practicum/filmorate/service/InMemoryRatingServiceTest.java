package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("inmemory")
// Сбрасывать контекст между методами, чтобы тесты работали изолированно от результатов друг друга.
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class InMemoryRatingServiceTest extends BaseRatingServiceTest {

    private final RatingService ratingService;

    @Override
    protected RatingService getRatingService() {
        return ratingService;
    }
}


