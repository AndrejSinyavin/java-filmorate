package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.entity.Film;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для служб, работающих с фильмотекой.
 */
public interface FilmRepository {

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
     * Метод возвращает список всех записей о фильмах в фильмотеке.
     *
     * @return список фильмов, может быть пустым
     */
    List<Film> getFilms();

    /**
     * Метод возвращает запись о фильме по его ID.
     *
     * @param filmId ID искомого фильма
     * @return запись о фильме; либо пустое значение, если запись о фильме не найдена в хранилище
     */
    Optional<Film> getFilm(int filmId);

}
