package ru.yandex.practicum.filmorate.interfaces;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для служб, работающих с рейтингом фильмов.
 */
public interface LikesService {

    Optional<Integer> likeFilm(int filmId, int userId);

    Optional<Integer> unlikeFilm(int filmId, int userId);

    Optional<List<Integer>> getPopularFilm(int topSize);

    Optional<String> deleteFilmRate(int filmId);

    Optional<Integer> getFilmRate(int filmId);

    Optional<String> createFilmRate(int filmId, int rate);

    public Optional<String> updateFilmRate(int filmId, int rate);
}
