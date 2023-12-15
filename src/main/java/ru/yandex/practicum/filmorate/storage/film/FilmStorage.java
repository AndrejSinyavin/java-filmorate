package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

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
    List<Film> getFilms();

}
