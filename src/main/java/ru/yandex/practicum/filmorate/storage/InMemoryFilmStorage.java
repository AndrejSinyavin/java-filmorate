package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Хранилище и бизнес-логика работы с фильмотекой в памяти.
 */
@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    /**
     * Хранилище записей о фильмах.
     */
    private final Map<Integer, Film> films = new HashMap<>();
    /**
     * Счетчик ID для зарегистрированных фильмов в хранилище
     */
    private int countId;

    /**
     * Метод создает запись о фильме в хранилище.
     *
     * @param film запись о фильме, которую нужно создать в хранилище
     * @return этот же фильм с уже зарегистрированным ID в хранилище
     */
    @Override
    public @NotNull Film createfilm(@NotNull Film film) {
        int id = newId();
        film.setId(id);
        films.put(id, film);
        log.info("Выполнена запись в хранилище: {}", film);
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
            log.info("Фильм обновлен в хранилище: {}", film);
            return film;
        } else {
            log.warn("Фильм не найден в хранилище!");
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
        ArrayList<Film> list = new ArrayList<>(films.values());
        log.info("Возвращен список всех фильмов из хранилища: {}", list);
        return list;
    }

    /**
     * Метод авто-генерации ID для создаваемых в фильмотеке фильмов
     *
     * @return новый ID
     */
    private int newId() {
        return ++countId;
    }
}
