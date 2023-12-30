package ru.yandex.practicum.filmorate.services.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.InternalServiceException;
import ru.yandex.practicum.filmorate.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.interfaces.LikesService;
import ru.yandex.practicum.filmorate.models.Film;

import java.util.LinkedList;
import java.util.List;

/**
 * Сервис содержит логику работы с пользователями
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class FilmService {
    private final String thisService = this.getClass().getName();
    /**
     * Подключение сервиса работы с фильмами.
     */
    private final FilmStorage films;
    /**
     * Подключение сервиса работы с "лайками".
     */
    private final LikesService likes;

    /**
     * Метод позволяет пользователю лайкнуть фильм.
     *
     * @param filmId ID фильма
     * @param userId ID пользователя
     */
    public void addLike(int filmId, int userId) {
        log.info("Добавление лайка фильму:");
        Film film = films.getFilm(filmId).orElseThrow(() -> new EntityNotFoundException(
                thisService, films.getClass().getName(),
                String.format("Запись о фильме c ID %d на сервисе не найдена", userId)));
        film.setRate(likes.likeFilm(filmId, userId).orElseThrow(() -> new EntityAlreadyExistsException(
                thisService, likes.getClass().getName(), "Пользователь уже ставил лайк фильму!")));
    }

    /**
     * Метод позволяет пользователю удалить ранее поставленный лайк фильму.
     *
     * @param filmId ID фильма
     * @param userId ID пользователя
     */
    public void deleteLike(int filmId, int userId) {
        log.info("Удаление лайка фильму на сервисе:");
        Film film = films.getFilm(filmId).orElseThrow(() -> new EntityNotFoundException(
                thisService, films.getClass().getName(),
                String.format("Запись о фильме c ID %d на сервисе не найдена", userId)));
        film.setRate(likes.unlikeFilm(filmId, userId).orElseThrow(() -> new InternalServiceException(
                thisService, likes.getClass().getName(), "Пользователь уже отменил лайк фильму!")));
    }

    /**
     * Метод получает топ лучших фильмов по лайкам пользователей.
     *
     * @param topSize размер топа
     * @return топ лучших фильмов
     */
    public List<Film> getTopFilms(int topSize) {
        log.info("Получение списка наиболее популярных фильмов по количеству лайков, топ {}:", topSize);
        List<Integer> topFilmsId = likes.getPopularFilm(topSize).orElseThrow(
                () -> new InternalServiceException(thisService, likes.getClass().getName(),
                        "Ошибка сервиса, не удалось создать список популярных фильмов"));
        List<Film> topFilms = new LinkedList<>();
        topFilmsId.forEach(id -> topFilms.add(films.getFilm(id).orElseThrow(
                () -> new InternalServiceException(thisService, films.getClass().getName(),
                        "Ошибка сервиса, фильм есть в топе, но отсутствует в filmService"))));
        return topFilms;
    }

    /**
     * Метод создает запись о фильме на сервисе.
     *
     * @param film фильм
     * @return этот же фильм с установленным ID после регистрации
     */
    public Film createfilm(Film film) {
        log.info("Создание записи о фильме и его регистрация на сервисе: {}", film);
        film.setRate(0);
        var result = films.createfilm(film).orElseThrow(
                () -> new InternalServiceException(thisService, films.getClass().getName(),
                "Ошибка сервиса, не удалось создать запись о фильме в фильмотеке"));
        likes.createFilm(film.getId(), 0).ifPresent(error -> {
            throw new InternalServiceException(thisService, likes.getClass().getName(), error); });
        return result;
    }

    /**
     * Метод обновляет запись о фильме на сервисе.
     *
     * @param film обновленная запись о фильме
     * @return обновленная запись о фильме
     */
    public Film updateFilm(Film film) {
        log.info("Обновление записи о фильме на сервисе: {}", film);
        var result = films.updateFilm(film).orElseThrow(
                () -> new EntityNotFoundException(
                        thisService, films.getClass().getName(), "Запись о фильме на сервисе не найдена"));
        likes.updateFilm(film.getId(), film.getRate()).ifPresent(error -> {
            throw new InternalServiceException(thisService, likes.getClass().getName(), error); });
        return result;
    }

    /**
     * Метод удаляет запись о фильме с сервиса.
     *
     * @param id ID удаляемой записи
     */
    public void deleteFilm(int id) {
        log.info("Удаление с сервиса записи о фильме ID {}:", id);
        films.deleteFilm(id).orElseThrow(() -> new EntityNotFoundException(thisService, films.getClass().getName(),
                String.format("Удалить запись не удалось, фильм с ID %d не найден!", id)));
        likes.deleteFilm(id).ifPresent(message -> {
            throw new InternalServiceException(thisService, likes.getClass().getName(), message); });
    }

    /**
     * Метод возвращает список всех записей о фильмах.
     *
     * @return список фильмов
     */
    public List<Film> getFilms() {
        log.info("Получение списка всех фильмов сервиса:");
        var result = films.getFilms().orElseThrow(() -> new InternalServiceException(thisService, films.getClass().getName(),
                "Ошибка сервиса, не удалось получить список всех фильмов"));
        result.forEach(film ->
            film.setRate(likes.getFilmRate(film.getId()).get()));
        return result;
    }

    /**
     * Метод возвращает запись о конкретном фильме.
     *
     * @param id ID искомого фильма
     * @return найденная запись о фильме
     */
    public Film getFilm(int id) {
        log.info("Получение с сервиса записи о фильме:");
        var result = films.getFilm(id).orElseThrow(() -> new EntityNotFoundException(
                thisService, films.getClass().getName(),
                String.format("Получить запись о фильме не удалось, фильм с ID %d не найден!", id)));
        result.setRate(likes.getFilmRate(id).orElseThrow(() -> new InternalServiceException(
                thisService, likes.getClass().getName(), String.format("Информация о фильме ID %d не найдена!", id))));
        return result;
    }

}
