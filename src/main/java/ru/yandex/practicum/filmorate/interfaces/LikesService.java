package ru.yandex.practicum.filmorate.interfaces;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для служб, работающих с рейтингом фильмов.
 */
public interface LikesService {

    Optional<Integer> likeFilm(int filmId, int userId);

    Optional<Integer> disLikeFilm(int filmId, int userId);

    List<Integer> getPopularFilm(int topSize);

    void deleteFilm(int filmId);

    int unregisterFilm(int filmId);

    void registerFilm(int filmId, int rate);
}
