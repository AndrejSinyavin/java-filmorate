package ru.yandex.practicum.filmorate.storages.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.interfaces.RegistrationService;
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
     * @return этот же фильм с уже зарегистрированным ID в хранилище
     */
    @Override
    public Optional<Film> createfilm(@NotNull Film film) {
        log.info("Создание записи о фильме в хранилище:");
        films.put(registrationService.register(film), film);
        log.info("Фильм добавлен в хранилище: {}", film);
        return Optional.of(film);
    }

    /**
     * Метод обновляет в хранилище фильмов существующую запись о фильме.
     *
     * @param film фильм из запроса с установленным ID, по которому ищется этот фильм в хранилище.
     * @return обновленный фильм из хранилища
     */
    @Override
    public Optional<Film> updateFilm(@NotNull Film film) {
        log.info("Обновление записи о фильме в хранилище:");
        int id = film.getId();
        if (!films.containsKey(id)) {
            log.info("Запись о фильме не найдена в хранилище: {}", film);
            return Optional.empty();
        } else {
            films.put(id, film);
            log.info("Запись о фильме обновлена в хранилище: {}", film);
            return Optional.of(film);
        }
    }

    /**
     * Метод удаляет запись о фильме из хранилища в памяти.
     *
     * @param filmId ID фильма
     * @return true, если запись удалена, false - если такой записи не было
     */
    @Override
    public boolean deleteFilm(@Positive int filmId) {
        log.info("Удаление записи о фильме из хранилища:");
        if (Objects.isNull(films.remove((filmId)))) {
            log.warn("Удалить запись о фильме не удалось, фильм с ID {} не найден!", filmId);
            return false;
        } else {
            log.info("Запись о фильме ID {} удалена из хранилища", filmId);
            return true;
        }
    }

    /**
     * Метод возвращает список всех записей о фильмах из хранилища.
     *
     * @return возвращаемый список фильмов, может быть пустым
     */
    @Override
    public Optional<List<Film>> getFilms() {
        log.info("Получаем список всех записей о фильмах из хранилища");
        return Optional.of(List.copyOf(films.values()));
    }

    /**
     * Метод возвращает запись о фильме из хранилища в памяти.
     *
     * @param filmId ID фильма
     * @return запись о фильме
     */
    @Override
    public Optional<Film> getFilm(@Positive int filmId) {
        log.info("Получаем запись о фильме ID {} из хранилища", filmId);
        return Optional.ofNullable(films.get(filmId));
    }

}
