package ru.yandex.practicum.filmorate.storages.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.interfaces.RegistrationService;
import ru.yandex.practicum.filmorate.models.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Хранилище и бизнес-логика работы с фильмотекой в памяти.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {
    private static final String FILM_STORAGE_INTERNAL_ERROR =
            "Выполнение операций с фильмами в хранилище в памяти";
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
    public Film createfilm(Film film) {
        log.info("Создание записи о фильме в хранилище:");
        films.put(registrationService.register(film), film);
        log.info("Фильм добавлен в хранилище: {}", film);
        return film;
    }

    /**
     * Метод обновляет в хранилище фильмов существующую запись о фильме.
     *
     * @param film фильм из запроса с установленным ID, по которому ищется этот фильм в хранилище.
     * @return обновленная запись - фильм из хранилища
     */
    @Override
    public Film updateFilm(Film film) {
        log.info("Обновление записи о фильме в хранилище:");
        int id = film.getId();
        if (films.containsKey(id)) {
            films.put(id, film);
            log.info("Запись о фильме обновлена в хранилище: {}", film.getName());
            return film;
        } else {
            String message = "Запись о фильме в хранилище не найдена";
            log.warn(message);
            throw new EntityNotFoundException(this.getClass().getName(), FILM_STORAGE_INTERNAL_ERROR, message);
        }
    }

    /**
     * Метод удаляет запись о фильме из хранилища в памяти.
     *
     * @param filmId ID фильма
     * @return ссылка на удаленную запись
     */
    @Override
    public Film deleteFilm(int filmId) {
        log.info("Удаление записи о фильме из хранилища");
        Film film = films.remove((filmId));
        if (film == null) {
            String message =
                    String.format("Удалить запись о фильме не удалось, фильм с ID %d не найден!", filmId);
            log.warn(message);
            throw new EntityNotFoundException(this.getClass().getName(), FILM_STORAGE_INTERNAL_ERROR, message);
        } else {
            log.warn("Запись о фильме ID {} удалена из хранилища", filmId);
            return film;
        }
    }

    /**
     * Метод возвращает список всех записей о фильмах из хранилища.
     *
     * @return возвращаемый список фильмов, может быть пустым
     */
    @Override
    public List<Film> getFilms() {
        log.info("Возвращен список всех фильмов из хранилища");
        return List.copyOf(films.values());
    }

    /**
     * Метод возвращает запись о фильме из хранилища в памяти.
     *
     * @param filmId ID фильма
     * @return ссылка на запись о фильме
     */
    @Override
    public Film getFilm(int filmId) {
        log.info("Получение записи о фильме из хранилища");
        Film film = films.get(filmId);
        if (film == null) {
            String message =
                    String.format("Получить запись о фильме не удалось, фильм с ID %d не найден!", filmId);
            log.warn(message);
            throw new EntityNotFoundException(this.getClass().getName(), FILM_STORAGE_INTERNAL_ERROR, message);
        }
        log.info("Получен фильм ID {}", filmId);
        return film;
    }
}
