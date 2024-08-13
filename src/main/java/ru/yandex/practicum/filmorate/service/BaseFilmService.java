package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.entity.Film;

import java.util.List;

public interface BaseFilmService {
    /**
     * Метод позволяет пользователю лайкнуть фильм.
     *
     * @param filmId ID фильма
     * @param userId ID пользователя
     */
    void addLike(int filmId, int userId);

    /**
     * Метод позволяет пользователю удалить ранее поставленный лайк фильму.
     *
     * @param filmId ID фильма
     * @param userId ID пользователя
     */
    void deleteLike(int filmId, int userId);

    /**
     * Метод получает топ лучших фильмов по лайкам пользователей.
     *
     * @param topSize размер топа
     * @param genreId идентификатор жанра
     * @param year    год релиза фильма
     * @return топ лучших фильмов
     */
    List<Film> getTopFilms(Integer topSize, Integer genreId, Integer year);

    /**
     * Метод создает запись о фильме на сервисе.
     *
     * @param film фильм
     * @return этот же фильм с установленным ID после регистрации
     */
    Film createfilm(Film film);

    /**
     * Метод обновляет запись о фильме на сервисе.
     *
     * @param film обновленная запись о фильме
     * @return обновленная запись о фильме
     */
    Film updateFilm(Film film);

    /**
     * Метод возвращает список всех записей о фильмах.
     *
     * @return список фильмов
     */
    List<Film> getFilms();

    /**
     * Возвращает список фильмов режиссера, отсортированный по заданному критерию.
     *
     * @param directorId режиссер
     * @param criteria   критерий сортировки
     * @return список фильмов этого режиссера, отсортированный по критерию
     */
    List<Film> getFilmsSortedByCriteria(int directorId, String criteria);

    /**
     * Метод возвращает запись о конкретном фильме.
     *
     * @param id ID искомого фильма
     * @return найденная запись о фильме
     */
    Film getFilm(int id);

    /**
     * Метод возвращает список общих с другом фильмов с сортировкой по их популярности
     *
     * @param userId   идентификатор пользователя, запрашивающего информацию
     * @param friendId идентификатор пользователя, с которым необходимо сравнить список фильмов
     * @return возвращает список фильмов, отсортированных по популярности.
     */
    List<Film> getCommonFilms(int userId, int friendId);

    void deleteFilm(int id);

    //Метод для поиска фильмов по режисеру и/или названию, в том числе по подстроке
    List<Film> getFilmsByTitleAndDirector(String query, String searchParameters);
}
