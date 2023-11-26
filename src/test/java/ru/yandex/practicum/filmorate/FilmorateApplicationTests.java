package ru.yandex.practicum.filmorate;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Log4j2
class FilmorateApplicationTests {
	private static Validator validator;
	private Set<ConstraintViolation<Film>> violations;
	private Film film;

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
		film.setReleaseDate("2002-12-06");
		log.info("=====================");
	}

	@AfterEach
	public void viewViolationMessages() {
		log.info(film);
		for (ConstraintViolation<Film> violation : violations) {
			log.error(violation.getMessage());
		}
	}
	@Test
	@DisplayName("Все поля фильма корректны")
	void filmNormalTest() {
		violations = validator.validate(film);
		assertTrue(violations.isEmpty());
	}

	@Test
	@DisplayName("Все поля фильма пустые, недопустимо")
	void filmDefaultTest() {
		film = new Film();
		violations = validator.validate(film);
		assertFalse(violations.isEmpty());
	}

	@Test
	@DisplayName("Пустое название фильма, недопустимо")
	void emptyNameTest() {
		film.setName("");
		violations = validator.validate(film);
		assertFalse(violations.isEmpty());
	}

	@Test
	@DisplayName("Название фильма из пробелов, недопустимо")
	void blankNameTest() {
		film.setName(" ");
		violations = validator.validate(film);
		assertFalse(violations.isEmpty());
	}

	@Test
	@DisplayName("Название фильма null, недопустимо")
	void filmNullTest() {
		film = new Film();
		violations = validator.validate(film);
		assertFalse(violations.isEmpty());
	}

	@Test
	@DisplayName("Описание фильма null (пустое), допустимо")
	void descriptionNullTest() {
		film.setDescription(null);
		violations = validator.validate(film);
		assertTrue(violations.isEmpty());
	}

	@Test
	@DisplayName("Описание фильма пустое, допустимо")
	void descriptionEmptyTest() {
		film.setDescription("");
		violations = validator.validate(film);
		assertTrue(violations.isEmpty());
	}

	@Test
	@DisplayName("Описание фильма не пустое, допустимо")
	void descriptionNotEmptyTest() {
		film.setDescription(" ");
		violations = validator.validate(film);
		assertTrue(violations.isEmpty());
	}

	@Test
	@DisplayName("Описание фильма максимально допустимого размера, допустимо")
	void descriptionMaxSizeTest() {
		film.setDescription(" ");
		violations = validator.validate(film);
		assertTrue(violations.isEmpty());
	}
}
