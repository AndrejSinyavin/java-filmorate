package ru.yandex.practicum.filmorate.services;

import ru.yandex.practicum.filmorate.exceptions.AppException;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для служб, работающих с рейтингом фильмов.
 */
public interface LikesService {

    /**
     * Пользователь ставит лайк фильму.
     *
     * @param filmId фильм
     * @param userId пользователь
     * @return сформированное исключение, если лайк не поставлен; пустое значение, если операция выполнена успешно
     */
    Optional<? extends AppException> likeFilm(int filmId, int userId);

    /**
     * Пользователь отменяет лайк фильму.
     *
     * @param filmId фильм
     * @param userId пользователь
     * @return сформированное исключение, если лайк не поставлен; пустое значение, если операция выполнена успешно
     */
    Optional<? extends AppException> unlikeFilm(int filmId, int userId);

    /**
     * Метод вызывается при создании фильма в фильмотеке. Регистрирует фильм в сервисе LikesService.
     *
     * @param filmId ID фильма
     * @param rate   рейтинг фильма
     * @return рейтинг фильма, или пустое значение - если ошибка
     */
    Optional<? extends AppException> registerFilm(int filmId, int rate);

    /**
     * Метод вызывается при удалении фильма из фильмотеки. Отменяет регистрацию фильма в сервисе LikesService.
     *
     * @param filmId ID фильма
     * @return пустое значение, если операция завершена успешно, иначе сообщение с ошибкой.
     */
    Optional<? extends AppException> unregisterFilm(int filmId);

    /**
     * Метод вызывается при обновлении фильма в фильмотеке.
     *
     * @param filmId ID фильма
     * @param rate   рейтинг фильма
     * @return пустое значение, если регистрация выполнена; иначе - сформированное исключение с ошибкой
     */
    Optional<? extends AppException> updateFilm(int filmId, int rate);

    /**
     * Метод возвращает рейтинг фильма
     *
     * @param filmId ID фильма
     * @return пустое значение, если операция завершена успешно, иначе сформированное исключение
     */
    Optional<Integer> getFilmRate(int filmId);

    /**
     * Метод возвращает топ рейтинга фильмов по количеству лайков
     *
     * @param topSize размер топа
     * @return список ID фильмов топа в порядке убывания количества лайков
     */
    Optional<List<Integer>> getPopularFilm(int topSize);

    /**
     * Метод вызывается при создании пользователя в фильмотеке. Регистрирует пользователя в LikesService.
     *
     * @param userId ID пользователя
     * @return пустое значение, если операция завершена успешно, иначе сообщение об ошибке
     */
    Optional<String> registerUser(int userId);

    /**
     * Метод вызывается при удалении пользователя из фильмотеки. Отменяет регистрацию пользователя в LikesService.
     *
     * @param userId ID пользователя
     * @return пустое значение, если операция завершена успешно, иначе сообщение об ошибке
     */
    Optional<String> unregisterUser(int userId);
}