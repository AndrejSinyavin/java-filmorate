package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.entity.*;
import ru.yandex.practicum.filmorate.repository.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@ComponentScan
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("Тесты работы с БД")
class FilmoRateApplicationTests {
    private final FilmRepository films;
    private final FriendRepository friends;
    private final UserRepository users;
    private final LikeRepository likes;
    private final UtilRepository utils;
    private Optional<Film> film;
    private Optional<User> user;
    private List<Film> listFilm;
    private Genre genre;
    private Mpa mpa;

    @Test
    @DisplayName("Фильм1 читается из БД")
    public void testGetFilm() {

        film = films.getFilm(1);

        assertThat(film)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1));
    }

    @Test
    @DisplayName("Фильм6 не существует в БД")
    public void testGetUnknownFilm() {

        film = films.getFilm(6);

        assertThat(film).isNotPresent();
    }

    @Test
    @DisplayName("В БД есть 5 фильмов")
    public void testGetFilms() {

        var listFilms = films.getFilms();

        assertThat(listFilms).hasSize(5);
    }

    @Test
    @DisplayName("В БД корректно создан и затем прочитан новый фильм")
    public void testCreateFilm() {
        genre = utils.getGenreById(1);
        mpa = utils.getMpaById(1);
        Film testFilm = new Film(
                0,
                "name",
                "description",
                LocalDate.of(2024, 7, 29),
                120,
                1000,
                mpa,
                List.of(genre));

        films.createFilm(testFilm);
        film = films.getFilm(6);

        assertThat(film)
                .isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film).hasFieldOrPropertyWithValue("id", 6);
                    assertThat(film).hasFieldOrPropertyWithValue("name", testFilm.getName());
                    assertThat(film).hasFieldOrPropertyWithValue("description", testFilm.getDescription());
                    assertThat(film).hasFieldOrPropertyWithValue("releaseDate", testFilm.getReleaseDate());
                    assertThat(film).hasFieldOrPropertyWithValue("duration", testFilm.getDuration());
                    assertThat(film).hasFieldOrPropertyWithValue("rate", 0);
                    assertThat(film).hasFieldOrPropertyWithValue("mpa", testFilm.getMpa());
                });
    }

    @Test
    @DisplayName("Обновляем фильм c ID 5")
    public void testUpdateFilm() {
        film = films.getFilm(5);
        var testFilm = film.get();
        testFilm.setDescription("NEWdescription");
        films.updateFilm(testFilm);
        film = films.getFilm(5);

        assertThat(film)
                .isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film).hasFieldOrPropertyWithValue("id", 5);
                    assertThat(film).hasFieldOrPropertyWithValue("description", testFilm.getDescription());
                });
    }

    @Test
    @DisplayName("Проверяем топ-2 рейтинга фильмов")
    public void testGetPopularFilm() {
        likes.likeFilm(5,1);
        likes.likeFilm(5,2);
        likes.likeFilm(4,3);
        var top = films.getPopularFilm(2);
        assertThat(top).hasSize(2);
        var testFilm = top.getFirst();
        assertThat(testFilm).hasFieldOrPropertyWithValue("id", 5);
        assertThat(testFilm).hasFieldOrPropertyWithValue("rate", 2);
        testFilm = top.getLast();
        assertThat(testFilm).hasFieldOrPropertyWithValue("id", 4);
        assertThat(testFilm).hasFieldOrPropertyWithValue("rate", 1);
    }
}