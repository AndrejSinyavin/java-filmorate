package ru.yandex.practicum.filmorate.storage;

import lombok.extern.log4j.Log4j2;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;

/**
 * Хранилище и бизнес-логика работы с фильмотекой в памяти.
 */
@Log4j2
public class InMemoryFilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int countId;

    /**
     * Метод создает запись о фильме в хранилище
     * @param film запись о фильме, которую нужно создать в хранилище
     * @return этот же фильм с уже зарегистрированным ID в хранилище
     */
    public Film createfilm(Film film) {
        int id = newId();
        film.setId(id);
        films.put(id, film);
        log.info("Выполнена запись в фильмотеку, {}", film);
        return film;
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
