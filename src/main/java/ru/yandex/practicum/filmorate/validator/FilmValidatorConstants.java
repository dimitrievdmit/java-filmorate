package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Slf4j
public class FilmValidatorConstants {
    public static final int MAX_DESCRIPTION_LENGTH = 200;
    public static final String MIN_RELEASE_DATE_STR = "1895-12-28";
    public static final LocalDate MIN_RELEASE_DATE = LocalDate.parse(MIN_RELEASE_DATE_STR);
}
