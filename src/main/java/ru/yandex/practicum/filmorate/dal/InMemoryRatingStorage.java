package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.enums.FilmRating;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@SuppressWarnings("unused")
@Component
@Slf4j
@Profile("inmemory")  // аннотация @Qualifier в сервисах мешала настроить тесты сразу на обе реализации
public class InMemoryRatingStorage implements RatingStorage {

    @Override
    public Collection<FilmRating> getAll() {
        return new ArrayList<>(Arrays.asList(FilmRating.values()));
    }

    @Override
    public FilmRating getOne(Integer id) {
        return FilmRating.fromId(id);
    }
}
