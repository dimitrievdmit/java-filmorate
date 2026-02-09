package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest
@ActiveProfiles("db") // Подключаем БД профиль для работы с БД версиями хранилищ
@AutoConfigureTestDatabase
// Сбрасывать контекст между методами, чтобы тесты работали изолированно от результатов друг друга.
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DBGenreServiceTest extends BaseGenreServiceTest {

    private final GenreService genreService;

    @Override
    protected GenreService getGenreService() {
        return genreService;
    }
}

