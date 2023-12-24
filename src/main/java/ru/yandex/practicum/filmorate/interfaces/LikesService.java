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

    int getFilmRating(int filmId);

    void setFilmRating(int filmId, int rate);
}
