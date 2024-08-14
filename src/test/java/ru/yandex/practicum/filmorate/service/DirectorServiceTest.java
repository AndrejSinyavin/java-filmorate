package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.entity.Director;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.repository.JdbcDirectorRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import({DirectorService.class, JdbcDirectorRepository.class})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("Набор тестов для DirectorService")
class DirectorServiceTest {
    private final DirectorService directorService;
    private int id;
    private Director director;

    @Test
    @DisplayName("Сценарий проверок на получение всех режиссеров")
    void getAllDirectors() {
        id = directorService.createDirector(new Director(0,"create_7")).getId();
        for (int i = 1; i <= id; i++) {
            directorService.deleteDirector(i);
        }
        assertThat(directorService.getAllDirectors()).isEmpty();

    }

    @Test
    @DisplayName("Сценарий проверок на получение режиссера по ID")
    void getDirectorById() {
        id = directorService.createDirector(new Director(0, "director_2")).getId();
        directorService.deleteDirector(id);
        assertThrows(EntityNotFoundException.class, () -> directorService.getDirectorById(id));
        id = directorService.createDirector(new Director(0, "director_2")).getId();
        assertThat(directorService.getDirectorById(id))
                .isNotNull()
                .isInstanceOf(Director.class)
                .hasFieldOrPropertyWithValue("name", "director_2")
                .hasFieldOrPropertyWithValue("id", id);
    }

    @Test
    @DisplayName("Сценарий проверок на создание режиссера")
    void createDirector() {
        director = directorService.createDirector(new Director(0, "director_1"));
        id = director.getId();
        assertThat(director)
                .isNotNull()
                .isInstanceOf(Director.class)
                .hasFieldOrPropertyWithValue("name", "director_1")
                .hasFieldOrPropertyWithValue("id", id);
        assertThat(directorService.getDirectorById(id))
                .isNotNull()
                .isInstanceOf(Director.class)
                .hasFieldOrPropertyWithValue("name", "director_1")
                .hasFieldOrPropertyWithValue("id", id);
    }

    @Test
    @DisplayName("Сценарий проверок на обновление режиссера")
    void updateDirector() {
        id = directorService.createDirector(new Director(0, "director_3")).getId();
        assertThat(directorService.updateDirector(new Director(id, "director_updated")))
                .isNotNull()
                .isInstanceOf(Director.class)
                .hasFieldOrPropertyWithValue("name", "director_updated")
                .hasFieldOrPropertyWithValue("id", id);
        assertThat(directorService.getDirectorById(id))
                .isNotNull()
                .isInstanceOf(Director.class)
                .hasFieldOrPropertyWithValue("name", "director_updated")
                .hasFieldOrPropertyWithValue("id", id);
    }

    @Test
    @DisplayName("Сценарий проверок на удаление режиссера")
    void deleteDirector() {
        director = directorService.createDirector(new Director(0, "director_4"));
        int id = director.getId();
        assertThat(director)
                .isNotNull()
                .isInstanceOf(Director.class)
                .hasFieldOrPropertyWithValue("name", "director_4")
                .hasFieldOrPropertyWithValue("id", id);
        directorService.deleteDirector(id);
        assertThrows(EntityNotFoundException.class, () -> directorService.getDirectorById(id));
    }
}