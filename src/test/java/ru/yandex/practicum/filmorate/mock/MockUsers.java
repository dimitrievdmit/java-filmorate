package ru.yandex.practicum.filmorate.mock;

import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class MockUsers {
    public static final String VALID_EMAIL = "test@example.com";
    public static final String INVALID_EMAIL = "test.com";
    public static final String VALID_LOGIN = "validLogin";
    public static final String INVALID_LOGIN = "invalid login";
    public static final String VALID_NAME = "Valid Name";
    public static final LocalDate VALID_BIRTHDAY = LocalDate.now().minusYears(20);
    public static final LocalDate FUTURE_BIRTHDAY = LocalDate.now().plusDays(1);

    public static User getValidUser() {
        User user = new User();
        user.setEmail(VALID_EMAIL);
        user.setLogin(VALID_LOGIN);
        user.setName(VALID_NAME);
        user.setBirthday(VALID_BIRTHDAY);
        return user;
    }

    public static User getValidUser(Long id) {
        User user = getValidUser();
        user.setId(id);
        return user;
    }
}
