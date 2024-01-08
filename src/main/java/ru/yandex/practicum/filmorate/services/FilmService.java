package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.InternalServiceException;
import ru.yandex.practicum.filmorate.storages.FilmStorage;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.storages.LikeStorage;

import java.util.LinkedList;
import java.util.List;

/**
 * Сервис содержит логику работы с пользователями, используется контроллером FilmController.
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
    private final LikeStorage likes;

    /**
     * Метод позволяет пользователю лайкнуть фильм.
     *
     * @param filmId ID фильма
     * @param userId ID пользователя
     */
    public void addLike(int filmId, int userId) {
        log.info("Добавление лайка фильму на сервисе:");
        Film film = films.getFilm(filmId).orElseThrow(
                () -> new EntityNotFoundException(thisService, films.getClass().getName(),
                String.format("Запись о фильме c ID %d на сервисе не найдена", userId))
        );
        likes.likeFilm(filmId, userId).ifPresent(exception -> {
            throw exception; });
        film.setRate(likes.getFilmRate(filmId).orElseThrow(
                () -> new InternalServiceException(thisService, likes.getClass().getName(),
                        String.format("Ошибка сервиса: информация о фильме ID %d не найдена!", filmId))));
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
        likes.unlikeFilm(filmId, userId).ifPresent(exception -> {
            throw exception; });
        film.setRate(likes.getFilmRate(filmId).orElseThrow(
                () -> new InternalServiceException(thisService, likes.getClass().getName(),
                        String.format("Ошибка сервиса: информация о фильме ID %d не найдена!", filmId))));
    }

    /**
     * Метод получает топ лучших фильмов по лайкам пользователей.
     *
     * @param topSize размер топа
     * @return топ лучших фильмов
     */
    public List<Film> getTopFilms(int topSize) {
        log.info("Получение списка наиболее популярных фильмов по количеству лайков, топ {}:", topSize);
        List<Integer> topFilmsId = likes.getPopularFilm(topSize);
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
        film = films.createfilm(film).orElseThrow(
                () -> new EntityAlreadyExistsException(thisService, films.getClass().getName(),
                "Запись о фильме уже существует в фильмотеке"));
        // ToDo Тесты Postman допускают модификацию рейтинга фильма через поле rate со стороны клиента и используют
        //  это при проверках. Это нарушает безопасность всей системы рейтинга сервиса, в будущем нужно убрать
        //  поле rate из класса Film.
        likes.registerFilm(film.getId(), film.getRate()).ifPresent(exception -> {
            throw exception; });
        return film;
    }

    /**
     * Метод обновляет запись о фильме на сервисе.
     *
     * @param film обновленная запись о фильме
     * @return обновленная запись о фильме
     */
    public Film updateFilm(Film film) {
        log.info("Обновление записи о фильме на сервисе: {}", film);
        film = films.updateFilm(film).orElseThrow(
                () -> new EntityNotFoundException(thisService, films.getClass().getName(),
                        "Запись о фильме не найдена в фильмотеке."));
        // ToDo Тесты Postman допускают модификацию рейтинга фильма через поле rate со стороны клиента и используют
        //  это при проверках. Это нарушает безопасность всей системы рейтинга сервиса, в будущем нужно убрать
        //  поле rate из класса Film.
        likes.updateFilm(film.getId(), film.getRate()).ifPresent(exception -> {
            throw exception; });
        return film;
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
        likes.unregisterFilm(id).ifPresent(exception -> {
            throw exception; });
    }

    /**
     * Метод возвращает список всех записей о фильмах.
     *
     * @return список фильмов
     */
    public List<Film> getFilms() {
        log.info("Получение списка всех фильмов сервиса:");
        return films.getFilms().orElseThrow(() -> new InternalServiceException(
                thisService, films.getClass().getName(), "Ошибка сервиса, не удалось получить список всех фильмов"));
    }

    /**
     * Метод возвращает запись о конкретном фильме.
     *
     * @param id ID искомого фильма
     * @return найденная запись о фильме
     */
    public Film getFilm(int id) {
        log.info("Получение с сервиса записи о фильме:");
        return films.getFilm(id).orElseThrow(() -> new EntityNotFoundException(
                thisService, films.getClass().getName(),
                String.format("Получить запись о фильме не удалось, фильм с ID %d не найден!", id)));
    }

}
