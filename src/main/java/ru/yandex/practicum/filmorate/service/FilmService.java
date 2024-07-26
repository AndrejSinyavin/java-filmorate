package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.InternalServiceException;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.repository.LikeRepository;

import java.util.LinkedList;
import java.util.List;

/**
 * Сервис содержит логику работы с пользователями, используется контроллером FilmController.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class FilmService implements BaseFilmService {
    private final String thisService = this.getClass().getName();
    /**
     * Подключение сервиса работы с фильмами.
     */
    private final FilmRepository films;
    /**
     * Подключение сервиса работы с "лайками".
     */
    private final LikeRepository likes;

    /**
     * Метод позволяет пользователю лайкнуть фильм.
     *
     * @param filmId ID фильма
     * @param userId ID пользователя
     */
    @Override
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
    @Override
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
    @Override
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
    @Override
    public Film createfilm(Film film) {
        log.info("Создание записи о фильме: {}", film);
        films.createfilm(film);
        return film;
    }

    /**
     * Метод обновляет запись о фильме на сервисе.
     *
     * @param film обновленная запись о фильме
     * @return обновленная запись о фильме
     */
    @Override
    public Film updateFilm(Film film) {
        log.info("Обновление записи о фильме на сервисе: {}", film);
        film = films.updateFilm(film).orElseThrow(
                () -> new EntityNotFoundException(thisService, films.getClass().getName(),
                        "Запись о фильме не найдена в фильмотеке."));
        return film;
    }

    /**
     * Метод возвращает список всех записей о фильмах.
     *
     * @return список фильмов
     */
    @Override
    public List<Film> getFilms() {
        log.info("Получение списка всех фильмов сервиса:");
        return films.getFilms();
    }

    /**
     * Метод возвращает запись о конкретном фильме.
     *
     * @param id ID искомого фильма
     * @return найденная запись о фильме
     */
    @Override
    public Film getFilm(int id) {
        log.info("Получение с сервиса записи о фильме:");
        return films.getFilm(id).orElseThrow(() -> new EntityNotFoundException(
                thisService, films.getClass().getName(),
                String.format("Получить запись о фильме не удалось, фильм с ID %d не найден!", id)));
    }

}
