package ru.yandex.practicum.filmorate.storages;

import ru.yandex.practicum.filmorate.models.Film;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для служб, работающих с фильмотекой.
 */
public interface FilmStorage {

    /**
     * Метод создает запись о фильме в фильмотеке.
     *
     * @param film запись о фильме, которую нужно создать в фильмотеке
     * @return этот же фильм с уже зарегистрированным ID в фильмотеке, или пустое значение при ошибке
     */
    Optional<Film> createfilm(Film film);

    /**
     * Метод обновляет существующую запись о фильме в фильмотеке.
     *
     * @param film запись о фильме из запроса с установленным ID, по которому ищется этот фильм в фильмотеке.
     * @return обновленная запись о фильме, или пустое значение при ошибке
     */
    Optional<Film> updateFilm(Film film);

    /**
     * Метод удаляет запись о фильме по его ID.
     *
     * @param filmId ID удаляемого фильма
     * @return удаленная запись о фильме; пустое значение - если такой записи не было найдено
     */
    Optional<Film> deleteFilm(int filmId);

    /**
     * Метод возвращает список всех записей о фильмах в фильмотеке.
     *
     * @return список фильмов, может быть пустым
     */
    Optional<List<Film>> getFilms();

    /**
     * Метод возвращает запись о фильме по его ID.
     *
     * @param filmId ID искомого фильма
     * @return запись о фильме; либо пустое значение, если запись о фильме не найдена в хранилище
     */
    Optional<Film> getFilm(int filmId);

}
