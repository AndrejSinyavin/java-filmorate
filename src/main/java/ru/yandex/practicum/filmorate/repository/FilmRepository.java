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
     * @return этот же фильм с новым ID, или пустое значение, если фильм создать не удалось
     */
    Optional<Film> createFilm(Film film);

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

    /**
     * Метод возвращает список фильмов соответсвующих списку их ID
     *
     * @param filmsIds список ID искомых фильмов
     * @return записи о фильмах; либо пустой список, если записи не найдены в хранилище
     */
    List<Film> getFilmsByIds(List<Integer> filmsIds);

    /**
     * Метод возвращает топ рейтинга фильмов по количеству лайков
     *
     * @param topSize размер топа
     * @return список ID фильмов топа в порядке убывания количества лайков
     */
    List<Film> getPopularFilm(int topSize);

    /**
     * Метод возвращает список общих с другом фильмов с сортировкой по их популярности
     *
     * @param userId   идентификатор пользователя, запрашивающего информацию
     * @param friendId идентификатор пользователя, с которым необходимо сравнить список фильмов
     * @return возвращает список фильмов, отсортированных по популярности.
     */
    List<Film> getCommonFilms(int userId, int friendId);
}
