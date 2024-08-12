package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.entity.Director;
import ru.yandex.practicum.filmorate.exception.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.InternalServiceException;

import java.util.Optional;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@Import({JdbcDirectorRepository.class})
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("Набор тестов для JdbcDirectorRepository")
class JdbcDirectorRepositoryTest {
    private final JdbcDirectorRepository directorRepository;
    private int id;
    private Optional<Director> director = Optional.empty();

    @Test
    @DisplayName("Сценарий проверок на получение всех режиссеров")
    void findAll() {
        director = directorRepository.create(new Director(0,"create_7"));
        assertThat(director).isPresent();
        id = director.get().getId();
        for (int i = 1; i <= id; i++) {
            directorRepository.delete(i);
        }
        assertThat(directorRepository.findAll()).isEmpty();
        director = directorRepository.create(new Director(0,"create_7"));
        assertThat(director).isPresent();
        id = director.get().getId();
        assertThat(directorRepository.findAll())
                .hasSize(1)
                .isInstanceOf(TreeSet.class);
        assertThat(directorRepository.findAll().stream().findFirst())
                .isPresent()
                .isInstanceOf(Optional.class)
                .hasValueSatisfying(d -> {
                    assertThat(d).hasFieldOrPropertyWithValue("id", id);
                    assertThat(d).hasFieldOrPropertyWithValue( "name", "create_7");
                });
    }

    @Test
    @DisplayName("Сценарий проверок на получение режиссера по его ID")
    void findById() {
        director = directorRepository.create(new Director(0,"create_6"));
        assertThat(director).isPresent();
        id = director.get().getId();
        directorRepository.delete(id);
        assertThat(directorRepository.findById(id)).isNotPresent();
        director = directorRepository.create(new Director(0,"create_6"));
        assertThat(director).isPresent();
        id = director.get().getId();
        assertThat(directorRepository.findById(id))
                .isPresent()
                .hasValueSatisfying(director -> {
                    assertThat(director).hasFieldOrPropertyWithValue("id", id);
                    assertThat(director).hasFieldOrPropertyWithValue( "name", "create_6");
                });
    }

    @Test
    @DisplayName("Сценарий проверок на создание режиссера")
    void create() {
        director = directorRepository.create(new Director(-1,"create_1"));
        assertThat(director).isPresent();
        id = director.get().getId();
        assertThat(directorRepository.findById(id))
                .isPresent()
                .hasValueSatisfying(director -> assertThat(director)
                        .hasFieldOrPropertyWithValue("id", id)
                        .hasFieldOrPropertyWithValue( "name", "create_1"));

        director = directorRepository.create(new Director(0,"create_2"));
        assertThat(director).isPresent();
        id = director.get().getId();
        assertThat(directorRepository.findById(id))
                .isPresent()
                .hasValueSatisfying(director -> assertThat(director)
                        .hasFieldOrPropertyWithValue("id", id)
                        .hasFieldOrPropertyWithValue( "name", "create_2"));

        director = directorRepository.create(new Director(0,"create_3"));
        assertThat(director).isPresent();
        id = director.get().getId();
        assertThat(directorRepository.findById(id))
                .isPresent()
                .hasValueSatisfying(director -> assertThat(director)
                        .hasFieldOrPropertyWithValue("id", id)
                        .hasFieldOrPropertyWithValue( "name", "create_3"));

        assertThrows(EntityAlreadyExistsException.class,
                () -> directorRepository.create(new Director(1,"create_3")));

        assertThrows(InternalServiceException.class, () -> directorRepository.create(null),
                "Ошибка! сущность режиссер = null");

        assertThrows(InternalServiceException.class, () -> directorRepository.create(new Director(0, null)),
                "Ошибка! сущность режиссер = null");
    }

    @Test
    @DisplayName("Сценарий проверок на обновление режиссера")
    void update() {
        director = directorRepository.create(new Director(0,"create_4"));
        assertThat(director).isPresent();
        id = director.get().getId();
        directorRepository.delete(id);
        assertThat(directorRepository.update(new Director(id,"NewName"))).isNotPresent();
        director = directorRepository.create(new Director(0,"create_4"));
        assertThat(director).isPresent();
        id = director.get().getId();
        assertThat(directorRepository.update(new Director(id,"NewName"))).isPresent();
        assertThat(directorRepository.findById(id))
                .isPresent()
                .hasValueSatisfying(director -> {
                    assertThat(director).hasFieldOrPropertyWithValue("id", id);
                    assertThat(director).hasFieldOrPropertyWithValue( "name", "NewName");
                });
    }

    @Test
    @DisplayName("Сценарий проверок на удаление режиссера")
    void delete() {
        director = directorRepository.create(new Director(0,"create_5"));
        assertThat(director).isPresent();
        id = director.get().getId();
        directorRepository.delete(id);
        assertThat(directorRepository.findById(id)).isNotPresent();
    }
}