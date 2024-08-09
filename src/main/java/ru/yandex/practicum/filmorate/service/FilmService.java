package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.InternalServiceException;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.LikeRepository;

import java.util.List;

/**
 * Сервис содержит логику работы с пользователями
 */
@Log4j2
@Valid
@Service
@RequiredArgsConstructor
public class FilmService implements BaseFilmService {
    private final String thisService = this.getClass().getName();
    /**
     * Подключение репозитория для работы с фильмами.
     */
    private final FilmRepository films;
    /**
     * Подключение репозитория для работы с "лайками".
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
        log.info("Добавление лайка фильму на сервисе");
        likes.likeFilm(filmId, userId);
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
        likes.unLikeFilm(filmId, userId);
    }

    /**
     * Метод получает топ лучших фильмов по лайкам пользователей.
     *
     * @param topSize размер топа
     * @return топ лучших фильмов
     */
    @Override
    public List<Film> getTopFilms(Integer topSize, Integer genreId, Integer year) {
        log.info("Получение списка наиболее популярных фильмов по количеству лайков, топ {}:", topSize);
        return films.getPopularFilm(topSize, genreId, year);
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
        return films.createFilm(film).orElseThrow(
                () -> new InternalServiceException(thisService, films.getClass().getName(),
                        "Не удалось создать запись на сервисе."));
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
        return films.updateFilm(film).orElseThrow(
                () -> new EntityNotFoundException(thisService, films.getClass().getName(),
                        "Обновить запись о фильме не удалось, запись не найдена на сервисе."));
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

    /**
     * Метод возвращает список общих с другом фильмов с сортировкой по их популярности
     *
     * @param userId   идентификатор пользователя, запрашивающего информацию
     * @param friendId идентификатор пользователя, с которым необходимо сравнить список фильмов
     * @return возвращает список фильмов, отсортированных по популярности.
     */
    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        return films.getCommonFilms(userId, friendId);
    }
}
