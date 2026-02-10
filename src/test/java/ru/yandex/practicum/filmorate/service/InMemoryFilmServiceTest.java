package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("inmemory")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class InMemoryFilmServiceTest extends BaseFilmServiceTest {

    private final FilmService filmService;
    private final UserService userService;

    @Override
    protected FilmService getFilmService() {
        return filmService;
    }

    @Override
    protected UserService getUserService() {
        return userService;
    }
}


