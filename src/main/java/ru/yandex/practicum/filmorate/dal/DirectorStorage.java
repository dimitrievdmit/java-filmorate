package ru.yandex.practicum.filmorate.dal;

import ru.yandex.practicum.filmorate.model.Director;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DirectorStorage {
    Director createDirector(Director director);

    void updateDirector(Director director);

    void deleteDirector(long id);

    Optional<Director> getDirector(long id);

    List<Director> getAllDirectors();

    boolean checkIfNotExists(Long id);

    void updateDirectorsForFilm(Set<Long> directorIds, long filmId, Boolean reset);

    boolean checkIfDirectorNotExists(Long id);
}
