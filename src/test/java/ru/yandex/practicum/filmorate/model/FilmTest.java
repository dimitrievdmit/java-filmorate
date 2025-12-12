package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.validator.FilmValidator;
import ru.yandex.practicum.filmorate.validator.annotation.AfterSpecifiedDate;

import java.lang.annotation.Annotation;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FilmTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void shouldNotHaveValidationErrors() {
        Film film = new Film();
        film.setId(1L);
        film.setName("Inception");
        film.setDescription("A mind-bending thriller");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(148);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        // Проверяем, что сработало нужное кол-во аннотаций валидации
        assertEquals(0, violations.size());
    }

    @Test
    void shouldFailWhenNameIsBlank() {
        Film film = new Film();
        film.setId(1L);
        film.setName(""); // Blank name
        film.setDescription("A mind-bending thriller");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(148);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        // Проверяем, что сработало нужное кол-во аннотаций валидации
        assertEquals(1, violations.size());
        Class<? extends Annotation> actualAnnotation = violations
                .iterator()
                .next()
                .getConstraintDescriptor()
                .getAnnotation()
                .annotationType();
        // Проверяем, что сработала нужная аннотация валидации
        assertEquals(NotBlank.class, actualAnnotation);
    }

    @Test
    void shouldSucceedWhenDescriptionExactlyMaxLength() {
        Film film = new Film();
        film.setId(1L);
        film.setName("Inception");
        String tooLongDescription = "A".repeat(FilmValidator.MAX_DESCRIPTION_LENGTH);
        film.setDescription(tooLongDescription); // Exceeds max length
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(148);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        // Проверяем, что сработало нужное кол-во аннотаций валидации
        assertEquals(0, violations.size());
    }

    @Test
    void shouldFailWhenDescriptionIsTooLong() {
        Film film = new Film();
        film.setId(1L);
        film.setName("Inception");
        String tooLongDescription = "A".repeat(FilmValidator.MAX_DESCRIPTION_LENGTH + 1);
        film.setDescription(tooLongDescription); // Exceeds max length
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(148);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        // Проверяем, что сработало нужное кол-во аннотаций валидации
        assertEquals(1, violations.size());
        Class<? extends Annotation> actualAnnotation = violations
                .iterator()
                .next()
                .getConstraintDescriptor()
                .getAnnotation()
                .annotationType();
        // Проверяем, что сработала нужная аннотация валидации
        assertEquals(Size.class, actualAnnotation);
    }

    @Test
    void shouldFailWhenReleaseDateIsBeforeMinimum() {
        Film film = new Film();
        film.setId(1L);
        film.setName("Inception");
        film.setDescription("A mind-bending thriller");

        LocalDate beforeMinDate = FilmValidator.MIN_RELEASE_DATE.minusDays(1);
        film.setReleaseDate(beforeMinDate); // Before MIN_RELEASE_DATE
        film.setDuration(148);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        // Проверяем, что сработало нужное кол-во аннотаций валидации
        assertEquals(1, violations.size());
        Class<? extends Annotation> actualAnnotation = violations
                .iterator()
                .next()
                .getConstraintDescriptor()
                .getAnnotation()
                .annotationType();
        // Проверяем, что сработала нужная аннотация валидации
        assertEquals(AfterSpecifiedDate.class, actualAnnotation);
    }

    @Test
    void shouldFailWhenDurationIsNegative() {
        Film film = new Film();
        film.setId(1L);
        film.setName("Inception");
        film.setDescription("A mind-bending thriller");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(-150); // Negative duration

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        // Проверяем, что сработало нужное кол-во аннотаций валидации
        assertEquals(1, violations.size());
        Class<? extends Annotation> actualAnnotation = violations
                .iterator()
                .next()
                .getConstraintDescriptor()
                .getAnnotation()
                .annotationType();
        // Проверяем, что сработала нужная аннотация валидации
        assertEquals(Positive.class, actualAnnotation);
    }

    @Test
    void shouldFailWhenDurationIsZero() {
        Film film = new Film();
        film.setId(1L);
        film.setName("Inception");
        film.setDescription("A mind-bending thriller");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(0); // Zero duration

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        // Проверяем, что сработало нужное кол-во аннотаций валидации
        assertEquals(1, violations.size());
        Class<? extends Annotation> actualAnnotation = violations
                .iterator()
                .next()
                .getConstraintDescriptor()
                .getAnnotation()
                .annotationType();
        // Проверяем, что сработала нужная аннотация валидации
        assertEquals(Positive.class, actualAnnotation);
    }
}
