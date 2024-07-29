package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.entity.User;

import java.time.LocalDate;
import java.util.Set;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.yandex.practicum.filmorate.FilmorateApplicationTestsOld.Mode.FILM;
import static ru.yandex.practicum.filmorate.FilmorateApplicationTestsOld.Mode.USER;
import static ru.yandex.practicum.filmorate.config.FilmorateApplicationSettings.MAX_DESCRIPTION_LENGTH;
import static ru.yandex.practicum.filmorate.config.FilmorateApplicationSettings.VALID_RELEASE_DATE;

@Log4j2
class FilmorateApplicationTestsOld {
    private static Validator validator;
    private Set<ConstraintViolation<Film>> filmViolations;
    private Set<ConstraintViolation<User>> userViolations;
    private Film film = new Film();
    private User user;
    private Mode infoMode;

    @BeforeAll
    public static void init() {
        log.atInfo();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    public void setTestContext() {
        film = new Film();
        film.setId(1);
        film.setName("Эквилибриум");
        film.setDescription("В будущем люди лишены возможности выражать эмоции. Это цена, которую человечество... ");
        film.setDuration(107);
        film.setReleaseDate(LocalDate.parse("2002-12-06"));

        user = new User();
        user.setId(1);
        user.setLogin("user");
        user.setName("Андрей");
        user.setBirthday(LocalDate.parse("1976-02-18"));
        user.setEmail("andrejsinyavin@yandex.ru");
    }

    @AfterEach
    public void viewViolationMessages() {
        switch (infoMode) {
            case FILM:
                log.info("""

                                ID фильма: {}\tДата релиза: {}\tПродолжительность : {} минут
                                Название: {}\t Описание: {}""",
                        film.getId(), film.getReleaseDate(), film.getDuration(), film.getName(), film.getDescription());
                for (ConstraintViolation<Film> violation : filmViolations) {
                    log.error(violation.getMessage());
                }
                break;
            case USER:
                log.info("""

                                ID пользователя: {}\tЛогин: {}
                                Имя пользователя: {}\t Дата рождения: {}\tEmail: {}""",
                        user.getId(), user.getLogin(), user.getName(), user.getBirthday(), user.getEmail());
                for (ConstraintViolation<User> violation : userViolations) {
                    log.error(violation.getMessage());
                }
                break;
            case NONE:
                return;
            default:
                break;
        }
    }

    @Test
    @DisplayName("Все поля фильма корректны")
    void filmNormalTest() {
        filmViolations = validator.validate(film);
        assertTrue(filmViolations.isEmpty());
        infoMode = FILM;
    }

    @Test
    @DisplayName("Все поля фильма пустые, недопустимо")
    void filmDefaultTest() {
        film = new Film();
        filmViolations = validator.validate(film);
        assertFalse(filmViolations.isEmpty());
        infoMode = FILM;
    }

    @Test
    @DisplayName("Пустое название фильма, недопустимо")
    void emptyNameTest() {
        film.setName("");
        filmViolations = validator.validate(film);
        assertFalse(filmViolations.isEmpty());
        infoMode = FILM;
    }

    @Test
    @DisplayName("Название фильма из пробелов, недопустимо")
    void nameBlankTest() {
        film.setName(" ");
        filmViolations = validator.validate(film);
        assertFalse(filmViolations.isEmpty());
        infoMode = FILM;
    }

    @Test
    @DisplayName("Название фильма null, недопустимо")
    void nameNullTest() {
        film.setName(null);
        filmViolations = validator.validate(film);
        assertFalse(filmViolations.isEmpty());
        infoMode = FILM;
    }

    @Test
    @DisplayName("Описание фильма null (пустое), недопустимо")
    void descriptionNullTest() {
        film.setDescription(null);
        filmViolations = validator.validate(film);
        assertFalse(filmViolations.isEmpty());
        infoMode = FILM;
    }

    @Test
    @DisplayName("Описание фильма пустое, не допустимо")
    void descriptionEmptyTest() {
        film.setDescription("");
        filmViolations = validator.validate(film);
        assertFalse(filmViolations.isEmpty());
        infoMode = FILM;
    }

    @Test
    @DisplayName("Описание фильма не пустое и меньше предельного размера, допустимо")
    void descriptionNotEmptyTest() {
        film.setDescription("Тест");
        filmViolations = validator.validate(film);
        assertTrue(filmViolations.isEmpty());
        infoMode = FILM;
    }

    @Test
    @DisplayName("Описание фильма предельного размера, допустимо")
    void descriptionMaxSizeTest() {
        film.setDescription("Q".repeat(MAX_DESCRIPTION_LENGTH));
        filmViolations = validator.validate(film);
        assertTrue(filmViolations.isEmpty());
        infoMode = FILM;
    }

    @Test
    @DisplayName("Описание фильма с превышением предельного размера, недопустимо")
    void descriptionOverSizeTest() {
        film.setDescription("Z".repeat(MAX_DESCRIPTION_LENGTH) + "oversize");
        filmViolations = validator.validate(film);
        assertFalse(filmViolations.isEmpty());
        infoMode = FILM;
    }

    @Test
    @DisplayName("Дата релиза не задана, недопустимо")
    void releaseNullTest() {
        film.setReleaseDate(null);
        filmViolations = validator.validate(film);
        assertFalse(filmViolations.isEmpty());
        infoMode = FILM;
    }

    @Test
    @DisplayName("Дата релиза пустая, недопустимо")
    void releaseEmptyTest() {
        film.setReleaseDate(null);
        filmViolations = validator.validate(film);
        assertFalse(filmViolations.isEmpty());
        infoMode = FILM;
    }

    @Test
    @DisplayName("Корректный паттерн даты, дата в допустимых пределах, допустимо")
    void releaseCorrectTest() {
        film.setReleaseDate(LocalDate.parse("2000-01-01"));
        filmViolations = validator.validate(film);
        assertTrue(filmViolations.isEmpty());
        infoMode = FILM;
    }

    @Test
    @DisplayName("Предельная дата, допустимо")
    void releaseMaxAllowableTest() {
        film.setReleaseDate(VALID_RELEASE_DATE);
        filmViolations = validator.validate(film);
        assertTrue(filmViolations.isEmpty());
        infoMode = FILM;
    }

    @Test
    @DisplayName("Дата раньше предельной, недопустимо")
    void releaseNotAllowableTest() {
        film.setReleaseDate(LocalDate.parse("1895-12-27"));
        filmViolations = validator.validate(film);
        assertFalse(filmViolations.isEmpty());
        infoMode = FILM;
    }

    @Test
    @DisplayName("Дата позже сегодняшней - анонс релиза, допустимо")
    void releaseAfterTomorrowTest() {
        film.setReleaseDate((LocalDate.now().plusDays(1)));
        filmViolations = validator.validate(film);
        assertTrue(filmViolations.isEmpty());
        infoMode = FILM;
    }

    @Test
    @DisplayName("Корректная длительность фильма, допустимо")
    void durationNormalTest() {
        film.setDuration(1);
        filmViolations = validator.validate(film);
        assertTrue(filmViolations.isEmpty());
        infoMode = FILM;
    }

    @Test
    @DisplayName("Нулевая длительность фильма, недопустимо")
    void durationIncorrectTest() {
        film.setDuration(0);
        filmViolations = validator.validate(film);
        assertFalse(filmViolations.isEmpty());
        infoMode = FILM;
    }

    @Test
    @DisplayName("Отрицательная длительность фильма, недопустимо")
    void durationNegativeTest() {
        film.setDuration(-1);
        filmViolations = validator.validate(film);
        assertFalse(filmViolations.isEmpty());
        infoMode = FILM;
    }

    @Test
    @DisplayName("Корректный ID, допустимо")
    void validIdTest() {
        film.setId(1);
        filmViolations = validator.validate(film);
        assertTrue(filmViolations.isEmpty());
        infoMode = FILM;
    }

    @Test
    @DisplayName("ID = 0, допустимо")
    void zeroIdTest() {
        film.setId(0);
        filmViolations = validator.validate(film);
        assertTrue(filmViolations.isEmpty());
        infoMode = FILM;
    }

    @Test
    @DisplayName("Отрицательный ID, недопустимо")
    void notValidIdTest() {
        film.setId(-1);
        filmViolations = validator.validate(film);
        assertFalse(filmViolations.isEmpty());
        infoMode = FILM;
    }

    @Test
    @DisplayName("Все поля пользователя корректны")
    void userNormalTest() {
        userViolations = validator.validate(user);
        assertTrue(userViolations.isEmpty());
        infoMode = USER;
    }

    @Test
    @DisplayName("Все поля пользователя пустые, недопустимо")
    void userDefaultTest() {
        user = new User();
        userViolations = validator.validate(user);
        assertFalse(userViolations.isEmpty());
        infoMode = USER;
    }

    @Test
    @DisplayName("Имя может отсутствовать, или быть любым, допустимо")
    void nameEmptyTest() {
        user.setName(null);
        userViolations = validator.validate(user);
        assertTrue(userViolations.isEmpty());
        infoMode = USER;
        viewViolationMessages();
        user.setName("");
        userViolations = validator.validate(user);
        assertTrue(userViolations.isEmpty());
        viewViolationMessages();
        user.setName(" ");
        userViolations = validator.validate(user);
        assertTrue(userViolations.isEmpty());
        viewViolationMessages();
        user.setName("ANDY");
        userViolations = validator.validate(user);
        assertTrue(userViolations.isEmpty());
    }

    @Test
    @DisplayName("Логин пустой, недопустимо")
    void loginEmptyTest() {
        user.setLogin(null);
        userViolations = validator.validate(user);
        assertFalse(userViolations.isEmpty());
        infoMode = USER;
        viewViolationMessages();
        user.setLogin("");
        userViolations = validator.validate(user);
        assertFalse(userViolations.isEmpty());
        viewViolationMessages();
        user.setLogin(" ");
        userViolations = validator.validate(user);
        assertFalse(userViolations.isEmpty());
    }

    @Test
    @DisplayName("Логин в наличии, допустимо")
    void loginNormalTest() {
        user.setLogin("AnDy");
        userViolations = validator.validate(user);
        assertTrue(userViolations.isEmpty());
        infoMode = USER;
    }

    @Test
    @DisplayName("Email некорректный, недопустимо")
    void emailIncorrectTest() {
        user.setEmail("andrejsinyavinyandex@");
        userViolations = validator.validate(user);
        assertFalse(userViolations.isEmpty());
        infoMode = USER;
    }

    @Test
    @DisplayName("Email корректный, допустимо")
    void emailCorrectTest() {
        user.setEmail("andrejsinyavin@yandex.ru");
        userViolations = validator.validate(user);
        assertTrue(userViolations.isEmpty());
        infoMode = USER;
    }

    @Test
    @DisplayName("Email пустой, недопустимо")
    void emailEmptyTest() {
        user.setEmail("");
        userViolations = validator.validate(user);
        assertFalse(userViolations.isEmpty());
        infoMode = USER;
        viewViolationMessages();
        user.setEmail(" ");
        userViolations = validator.validate(user);
        assertFalse(userViolations.isEmpty());
        viewViolationMessages();
        user.setEmail(null);
        userViolations = validator.validate(user);
        assertFalse(userViolations.isEmpty());
    }

    @Test
    @DisplayName("Дата рождения не может быть в будущем")
    void userBirthdayTest() {
        user.setBirthday(LocalDate.now().plusDays(1));
        userViolations = validator.validate(user);
        assertFalse(userViolations.isEmpty());
        infoMode = USER;
    }

    enum Mode {
        FILM,
        USER,
        NONE
    }

}
