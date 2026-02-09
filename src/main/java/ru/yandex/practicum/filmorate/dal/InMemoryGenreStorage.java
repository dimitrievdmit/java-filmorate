package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.enums.FilmGenre;

import java.util.*;

@SuppressWarnings("unused")
@Component
@Slf4j
@Profile("inmemory")  // аннотация @Qualifier в сервисах мешала настроить тесты сразу на обе реализации
public class InMemoryGenreStorage implements GenreStorage {

    @Override
    public Collection<FilmGenre> getAll() {
        return new ArrayList<>(Arrays.asList(FilmGenre.values()));
    }

    @Override
    public FilmGenre getOne(Integer id) {
        return FilmGenre.fromId(id);
    }
}
