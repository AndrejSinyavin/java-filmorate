package ru.yandex.practicum.filmorate.storages;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.services.RegistrationService;
import ru.yandex.practicum.filmorate.models.Film;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.*;

/**
 * Хранилище и бизнес-логика работы с фильмотекой в памяти.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {
    private static final String ID_ERROR = "ID может быть только положительным значением";
    private static final String ENTITY_ERROR = "Фильм не существует";
    /**
     * Хранилище записей о фильмах в памяти.
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
     * @return этот же фильм с уже зарегистрированным ID в хранилище, или пустое значение при ошибке
     */
    @Override
    public Optional<Film> createfilm(@NotNull(message = ENTITY_ERROR) Film film) {
        log.info("Создание записи о фильме в хранилище:");
        if (films.containsKey(film.getId())) {
            log.error("Запись о фильме уже существует");
            return Optional.empty();
        } else {
            films.put(registrationService.register(film), film);
            log.info("Запись о фильме создана в хранилище: {}", film);
            return Optional.of(film);
        }
    }

    /**
     * Метод обновляет в хранилище фильмов существующую запись о фильме.
     *
     * @param film фильм из запроса с установленным ID, по которому ищется этот фильм в хранилище.
     * @return обновленный фильм, если он уже существовал; иначе пустое значение
     */
    @Override
    public Optional<Film> updateFilm(@NotNull(message = ENTITY_ERROR) Film film) {
        log.info("Обновление записи о фильме в хранилище:");
        int id = film.getId();
        if (films.containsKey(id)) {
            films.put(id, film);
            log.info("Запись о фильме обновлена в хранилище: {}", film);
            return Optional.of(film);
        } else {
            log.error("Запись о фильме не найдена в хранилище: {}", film);
            return Optional.empty();
        }
    }

    /**
     * Метод удаляет запись о фильме из хранилища.
     *
     * @param filmId удаляемый фильм
     * @return удаленная запись о фильме; пустое значение - если такой записи не было найдено
     */
    @Override
    public Optional<Film> deleteFilm(@Positive(message = ID_ERROR) int filmId) {
        log.info("Удаление записи о фильме из хранилища:");
        Film deletedFilm = films.remove((filmId));
        if (deletedFilm == null) {
            log.error("Удалить запись о фильме не удалось, фильм с ID {} не найден!", filmId);
            return Optional.empty();
        } else {
            log.info("Запись о фильме удалена из хранилища: {}", deletedFilm);
            return Optional.of(deletedFilm);
        }
    }

    /**
     * Метод возвращает список всех записей о фильмах из хранилища.
     *
     * @return список фильмов, может быть пустым
     */
    @Override
    public Optional<List<Film>> getFilms() {
        var result = List.copyOf(films.values());
        log.info("Получен список всех записей о фильмах из хранилища, размер: {}", result.size());
        return Optional.of(result);
    }

    /**
     * Метод возвращает запись о фильме из хранилища в памяти.
     *
     * @param filmId ID фильма
     * @return запись о фильме; либо пустое значение, если запись о фильме не найдена в хранилище
     */
    @Override
    public Optional<Film> getFilm(@Positive(message = ID_ERROR) int filmId) {
        log.info("Получение записи о фильме ID {} из хранилища", filmId);
        return Optional.ofNullable(films.get(filmId));
    }

}
