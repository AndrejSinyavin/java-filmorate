package ru.yandex.practicum.filmorate.interfaces;

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
     * @return этот же фильм с уже зарегистрированным ID в фильмотеке
     */
    Film createfilm(Film film);

    /**
     * Метод обновляет существующую запись о фильме в фильмотеке.
     *
     * @param film фильм из запроса с установленным ID, по которому ищется этот фильм в фильмотеке.
     * @return обновленная запись - фильм из фильмотеки
     */
    Film updateFilm(Film film);

    /**
     * Метод возвращает список всех записей - фильмов в фильмотеке.
     *
     * @return возвращаемый список фильмов, может быть пустым
     */
    Optional<List<Film>> getFilms();

    /**
     * Метод возвращает запись - фильм по его ID.
     *
     * @param filmId ID фильма
     * @return фильм
     */
    Optional<Film> getFilm(int filmId);

    /**
     * Метод удаляет запись - фильм по его ID.
     *
     * @param filmId ID фильма
     * @return
     */
    boolean deleteFilm(int filmId);

}
