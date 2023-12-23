package ru.yandex.practicum.filmorate.interfaces;

import java.util.List;

/**
 * Интерфейс для служб, работающих с рейтингом фильмов.
 */
public interface LikesService {

    int likeFilm(int filmId, int userId);

    int disLikeFilm(int filmId, int userId);

    List<Integer> getPopularFilm(int topSize);

    void deleteFilm(int filmId);

    // ToDo: метод для тестов Postman, удалить в конечной реализации
    void backdoor(int filmId, int rate);
}
