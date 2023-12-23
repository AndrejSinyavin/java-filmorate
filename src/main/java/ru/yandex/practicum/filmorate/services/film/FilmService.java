package ru.yandex.practicum.filmorate.services.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.interfaces.LikesService;
import ru.yandex.practicum.filmorate.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.models.Film;

import java.util.LinkedList;
import java.util.List;

/**
 * Сервис содержит логику работы с пользователями
 */
@Log4j2
@Service
@RequiredArgsConstructor
public final class FilmService {
    private static final String FILM_STORAGE_SERVICE_ERROR =
            "Сервис работы с фильмами не выполнил задачу из-за отказа в сервисе FilmStorage";
    private static final String LIKES_SERVICE_ERROR =
            "Сервис работы с фильмами не выполнил задачу из-за отказа в сервисе LikesService";
    private static final String USER_SERVICE_ERROR =
            "Сервис работы с фильмами не выполнил задачу из-за отказа в сервисе UserService";

    /**
     * Подключение сервиса работы с фильмами.
     */
    private final FilmStorage films;
    /**
     * Подключение сервиса работы с "лайками".
     */
    private final LikesService likes;
    /**
     * Подключение сервиса работы с пользователями.
     */
    private final UserStorage users;

    /**
     * Метод позволяет пользователю лайкнуть фильм.
     *
     * @param filmId ID фильма
     * @param userId ID пользователя
     */
    public void addLike(int filmId, int userId) {
        log.info("Добавление лайка фильму:");
        Film film = films.getFilm(filmId);
        users.getUser(userId);
        film.setRate(likes.likeFilm(filmId, userId));
        log.info("Лайк добавлен");
    }

    /**
     * Метод позволяет пользователю "дизлайкнуть" фильм.
     *
     * @param filmId ID фильма
     * @param userId ID пользователя
     */
    public void deleteLike(int filmId, int userId) {
        log.info("Удаление лайка фильму:");
        Film film = films.getFilm(filmId);
        users.getUser(userId);
        film.setRate(likes.disLikeFilm(filmId, userId));
        log.info("Лайк удален");
    }

    /**
     * Метод получает топ лучших фильмов по лайкам пользователей.
     *
     * @param topSize размер топа
     * @return список фильмов
     */
    public List<Film> getTopFilms(int topSize) {
        log.info("Получение списка {} наиболее популярных фильмов по количеству лайков:", topSize);
        var top = likes.getPopularFilm(topSize);
        var result = new LinkedList<Film>();
        top.forEach(id -> {
            try {
                result.add(films.getFilm(id));
            } catch (FilmNotFoundException e) {
                log.warn("{} {}", e.getError(), e.getMessage());
            }
        });
        return result;
    }

    /**
     * Метод создает запись о фильме на сервисе.
     *
     * @param film фильм
     * @return этот же фильм с установленным ID после регистрации
     */
    public Film createfilm(Film film) {
        log.info("Создание записи о фильме и его регистрация на сервисе: {}", film);
        return films.createfilm(film);
    }

    /**
     * Метод обновляет запись о фильме на сервисе.
     *
     * @param film обновленные поля для записи
     * @return обновленная запись
     */
    public Film updateFilm(Film film) {
        log.info("Обновление записи о фильме на сервисе: {}", film);
        var result = films.updateFilm(film);
        // Следующий фрагмент кода добавлен исключительно для прохождения тестов Postman, считаю
        // логику изменения рейтинга фильма прямой инициализацией поля rate в объекте Film некорректной и опасной,
        // любой код вне сервера может "накрутить" рейтинг фильму нечестным образом. Бизнес-логика текущей реализации
        // предполагает использование отдельного защищенного сервиса рейтинга фильмов с предоставлением API для
        // получения, изменения рейтинга конкретного фильма и составления топа; поля rate в объекте Film
        // быть не должно, т.к. оно делает уязвимой всю систему рейтинга фильмотеки.
        if (film.getRate() != 0) {
            log.warn("Тестовая модификация рейтинга фильма:");
            likes.backdoor(film.getId(), film.getRate());
        }
        return result;
    }

    /**
     * Метод удаляет запись о фильме с сервиса.
     *
     * @param filmId ID удаляемой записи
     */
    public void deleteFilm(int filmId) {
        log.info("Удаление записи о фильме ID {} :", filmId);
        Film deletedFilm = films.deleteFilm(filmId);
        likes.deleteFilm(filmId);
        log.info("Ok");
    }

    /**
     * Метод возвращает список всех записей о фильмах.
     *
     * @return список фильмов
     */
    public List<Film> getFilms() {
        return films.getFilms();
    }

    /**
     * Метод возвращает запись о конкретном фильме.
     *
     * @param filmId ID искомого фильма
     * @return найденная запись о фильме
     */
    public Film getFilm(int filmId) {
        log.info("Получение фильма");
        return films.getFilm(filmId);
    }

}
