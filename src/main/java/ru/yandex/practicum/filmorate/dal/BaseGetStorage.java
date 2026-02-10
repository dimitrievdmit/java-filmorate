package ru.yandex.practicum.filmorate.dal;

import java.util.Collection;

public interface BaseGetStorage<T> {
    Collection<T> getAll();

    T getOne(Integer id);
}
