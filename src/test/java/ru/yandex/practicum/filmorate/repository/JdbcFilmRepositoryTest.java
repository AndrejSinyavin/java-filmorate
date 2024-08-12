package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.entity.*;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;


import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.yandex.practicum.filmorate.config.FilmorateApplicationSettings.DirectorSortParams.year;
import static ru.yandex.practicum.filmorate.config.FilmorateApplicationSettings.DirectorSortParams.likes;

@JdbcTest
@Import({JdbcFilmRepository.class, FilmService.class, UserService.class, JdbcLikeRepository.class,
        JdbcUtilRepository.class, JdbcUserRepository.class, JdbcFilmRepository.class, JdbcFriendRepository.class,
        DirectorService.class, JdbcDirectorRepository.class})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("Набор тестов для JdbcFilmRepository")
class JdbcFilmRepositoryTest {
    private final JdbcFilmRepository filmRepository;
    private final FilmService filmService;
    private final UserService userService;
    private final DirectorService directorService;
    private Optional<Film> film = Optional.empty();
    private int id;

    @Test
    @DisplayName("Сценарий проверок на создание фильма")
    void createFilm() {
        film = filmRepository.createFilm(null);
        assertThat(film).isNotPresent();
        film = filmRepository.createFilm(testFilm());
        assertThat(film).isPresent();
        id = film.get().getId();
        assertThat(filmRepository.getFilm(id))
                .isPresent()
                .hasValueSatisfying(film -> assertThat(film)
                        .hasFieldOrPropertyWithValue("id", id));
    }

    @Test
    @DisplayName("Сценарий проверок на изменение фильма")
    void updateFilm() {
        film = filmRepository.createFilm(testFilm());
        assertThat(film).isPresent();
        id = film.get().getId();
        Film modifiedFilm = film.get();
        modifiedFilm.setName("updatedName");
        assertThat(filmRepository.updateFilm(modifiedFilm)).isPresent();
        assertThat(filmRepository.getFilm(id))
                .isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film.getId() == id).isTrue();
                    assertThat("updatedName".equals(film.getName())).isTrue();
                });
    }

    @Test
    @DisplayName("Сценарий проверки чтения всех фильмов")
    void getFilms() {
        film = filmRepository.createFilm(testFilm());
        assertThat(film).isPresent();
        id = film.get().getId();
        int filmCount = 0;
        for (int i = 1; i <= id; i++) {
            if (filmRepository.getFilm(i).isPresent()) {
                filmCount++;
            }
        }
        assertThat(filmCount).isEqualTo(filmRepository.getFilms().size());
    }

    @Test
    @DisplayName("Сценарий проверок на получение фильма")
    void getFilm() {
        film = filmRepository.createFilm(testFilm());
        assertThat(film).isPresent();
        id = film.get().getId();
        assertThat(filmRepository.getFilm(id))
                .isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film.getId() == id).isTrue();
                    assertThat("film".equals(film.getName())).isTrue();
                });
    }

    @Test
    @DisplayName("Сценарий проверки на получение 'топа' популярных фильмов")
    void getPopularFilm() {
        var user1 = userService.createUser(new User(
                0,
                "user1",
                "name1",
                "user1@uandex.ru",
                LocalDate.of(2000, 1, 1)));
        var user2 = userService.createUser(new User(
                0,
                "user2",
                "name2",
                "user2@uandex.ru",
                LocalDate.of(2000, 1, 2)));
        var film1 = filmService.createfilm(testFilm());
        var film2 = filmService.createfilm(testFilm());
        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film1.getId(), user2.getId());
        filmService.addLike(film2.getId(), user2.getId());
        var top = filmService.getTopFilms(2, null, null);
        assertThat(top.size() == 2).isTrue();
        assertThat(top.getFirst().getId() == film1.getId()).isTrue();
        assertThat(top.getLast().getId() == film2.getId()).isTrue();
    }

    @Test
    @DisplayName("Сценарий получения 'топа' популярных фильмов режиссера по лайкам или годам")
    void findFilmsForDirectorByConditions() {
        var user1 = userService.createUser(new User(
                0,
                "user1",
                "name1",
                "user1@uandex.ru",
                LocalDate.of(2000, 1, 1)));
        var user2 = userService.createUser(new User(
                0,
                "user2",
                "name2",
                "user2@uandex.ru",
                LocalDate.of(2000, 1, 2)));
        var director = directorService.createDirector(new Director(0, "director"));
        int directorId = director.getId();
        var film1 = testFilm();
        film1.setDirectors(Collections.singleton(director));
        film1.setName("film1");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1 = filmService.createfilm(film1);
        var film2 = testFilm();
        film2.setDirectors(Collections.singleton(director));
        film2.setName("film2");
        film2.setReleaseDate(LocalDate.of(2010, 1, 1));
        film2 = filmService.createfilm(film2);
        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film1.getId(), user2.getId());
        filmService.addLike(film2.getId(), user2.getId());

        var sortedFilmsByCriteria = filmService.getFilmsSortedByCriteria(directorId, likes.name());
        assertThat(sortedFilmsByCriteria.size() == 2).isTrue();
        assertThat(sortedFilmsByCriteria.getFirst().getId() == film1.getId()).isTrue();
        assertThat(sortedFilmsByCriteria.getLast().getId() == film2.getId()).isTrue();

        sortedFilmsByCriteria = filmService.getFilmsSortedByCriteria(directorId, year.name());
        assertThat(sortedFilmsByCriteria.size() == 2).isTrue();
        assertThat(sortedFilmsByCriteria.getFirst().getId() == film1.getId()).isTrue();
        assertThat(sortedFilmsByCriteria.getLast().getId() == film2.getId()).isTrue();
    }


    @Test
    @DisplayName("Сценарий проверки удаления фильма")
    void deleteFilm() {
        film = filmRepository.createFilm(null);
        assertThat(film).isNotPresent();
        film = filmRepository.createFilm(testFilm());
        assertThat(film).isPresent();
        id = film.get().getId();
        assertThat(filmRepository.getFilm(id))
                .isPresent()
                .hasValueSatisfying(film -> assertThat(film)
                        .hasFieldOrPropertyWithValue("id", id));
        filmRepository.deleteFilmById(id);
        assertThat(filmRepository.getFilm(id)).isNotPresent();
    }

    private Film testFilm() {
        return new Film(
                0,
                "film",
                "description",
                LocalDate.now(),
                120,
                0,
                new Mpa(1, null),
                Collections.singletonList(new Genre(1, null)),
                new TreeSet<>(Director::compareTo));
    }
}