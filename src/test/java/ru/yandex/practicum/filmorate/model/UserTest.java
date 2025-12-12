package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;


class UserTest {

    private static Validator validator;
    private static final String VALID_EMAIL = "test@example.com";
    private static final String INVALID_EMAIL = "test.com";
    private static final String VALID_LOGIN = "validLogin";
    private static final String INVALID_LOGIN = "invalid login";
    private static final String VALID_NAME = "Valid Name";
    private static final LocalDate VALID_BIRTHDAY = LocalDate.now().minusYears(20);
    private static final LocalDate FUTURE_BIRTHDAY = LocalDate.now().plusDays(1);


    @BeforeAll
    static void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    public void shouldNotHaveValidationErrors() {
        User user = new User();
        user.setEmail(VALID_EMAIL);
        user.setLogin(VALID_LOGIN);
        user.setName(VALID_NAME);
        user.setBirthday(VALID_BIRTHDAY);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        // Проверяем, что сработало нужное кол-во аннотаций валидации
        assertEquals(0, violations.size());
    }

    @Test
    public void shouldFailWhenEmailIsBlank() {
        User user = new User();
        user.setEmail("");
        user.setLogin(VALID_LOGIN);
        user.setName(VALID_NAME);
        user.setBirthday(VALID_BIRTHDAY);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
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
    public void shouldFailWhenEmailIsInvalid() {
        User user = new User();
        user.setEmail(INVALID_EMAIL);
        user.setLogin(VALID_LOGIN);
        user.setName(VALID_NAME);
        user.setBirthday(VALID_BIRTHDAY);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        // Проверяем, что сработало нужное кол-во аннотаций валидации
        assertEquals(1, violations.size());
        Class<? extends Annotation> actualAnnotation = violations
                .iterator()
                .next()
                .getConstraintDescriptor()
                .getAnnotation()
                .annotationType();
        // Проверяем, что сработала нужная аннотация валидации
        assertEquals(Email.class, actualAnnotation);
    }

    @Test
    public void shouldFailWhenLoginIsBlank() {
        User user = new User();
        user.setEmail(VALID_EMAIL);
        user.setLogin("");
        user.setName(VALID_NAME);
        user.setBirthday(VALID_BIRTHDAY);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

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
    public void shouldFailWhenLoginContainsSpaces() {
        User user = new User();
        user.setEmail(VALID_EMAIL);
        user.setLogin(INVALID_LOGIN);
        user.setName(VALID_NAME);
        user.setBirthday(VALID_BIRTHDAY);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Проверяем, что сработало нужное кол-во аннотаций валидации
        assertEquals(1, violations.size());
        Class<? extends Annotation> actualAnnotation = violations
                .iterator()
                .next()
                .getConstraintDescriptor()
                .getAnnotation()
                .annotationType();
        // Проверяем, что сработала нужная аннотация валидации
        assertEquals(Pattern.class, actualAnnotation);
    }

    @Test
    public void shouldFailWhenBirthdayIsInTheFuture() {
        User user = new User();
        user.setEmail(VALID_EMAIL);
        user.setLogin(VALID_LOGIN);
        user.setName(VALID_NAME);
        user.setBirthday(FUTURE_BIRTHDAY);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        // Проверяем, что сработало нужное кол-во аннотаций валидации
        assertEquals(1, violations.size());
        Class<? extends Annotation> actualAnnotation = violations
                .iterator()
                .next()
                .getConstraintDescriptor()
                .getAnnotation()
                .annotationType();
        // Проверяем, что сработала нужная аннотация валидации
        assertEquals(PastOrPresent.class, actualAnnotation);
    }

}