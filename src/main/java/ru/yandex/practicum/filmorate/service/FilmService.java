package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.entity.Film;
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
        likes.undoLikeFilm(filmId, userId);
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
        return likes.getPopularFilm(topSize);
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
        films.createFilm(film);
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
        return films.updateFilm(film).orElseThrow(
                () -> new EntityNotFoundException(thisService, films.getClass().getName(),
                        "Запись о фильме не найдена в фильмотеке."));
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
     * Метод возвращает топ лучших фильмов, которые понравились пользователям.
     *
     * @param topSize Размер 'топа'
     * @return список анкет фильмов в порядке убывания количества 'лайков'
     */
    @Override
    public List<Film> getTopFilms(Integer topSize) {
        return List.of();
    }
}
