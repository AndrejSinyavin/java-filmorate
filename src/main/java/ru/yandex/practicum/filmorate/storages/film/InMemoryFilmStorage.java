package ru.yandex.practicum.filmorate.storages.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.interfaces.RegistrationService;
import ru.yandex.practicum.filmorate.models.Film;

import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * Хранилище и бизнес-логика работы с фильмотекой в памяти.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {
    /**
     * Хранилище записей о фильмах.
     */
    private final Map<Integer, Film> films = new HashMap<>();
    /**
     * Подключение службы регистрации пользователя в фильмотеке
     */
    private final RegistrationService<Film> registrationService;

    /**
     * Метод создает запись о фильме в хранилище.
     *
     * @param film запись о фильме, которую нужно создать в хранилище
     * @return этот же фильм с уже зарегистрированным ID в хранилище
     */
    @Override
    public @NotNull Film createfilm(@NotNull Film film) {
        int id = registrationService.register(film);
        films.put(id, film);
        log.info("Выполнена запись информации о фильме в хранилище: {}", film.getName());
        return film;
    }

    /**
     * Метод обновляет существующую запись о фильме в хранилище.
     *
     * @param film фильм из запроса с установленным ID, по которому ищется этот фильм в хранилище.
     * @return обновленная запись - фильм из хранилища
     */
    @Override
    public @NotNull Film updateFilm(@NotNull Film film) {
        int id = film.getId();
        if (films.containsKey(id)) {
            films.put(id, film);
            log.info("Запись о фильме обновлена в хранилище: {}", film.getName());
            return film;
        } else {
            log.warn("Запись о фильме не найдена в хранилище!");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Метод возвращает список всех записей - фильмов в хранилище.
     *
     * @return возвращаемый список фильмов, может быть пустым
     */
    @Override
    public @NotNull List<Film> getFilms() {
        log.info("Возвращен список всех фильмов из хранилища");
        return List.copyOf(films.values());
    }

}
