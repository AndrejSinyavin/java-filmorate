package ru.yandex.practicum.filmorate.interfaces;

import java.util.List;

public interface LikesService {
    boolean registerFilm(int filmId);

    boolean unregisterFilm(int userId);

    boolean likeFilm(int filmId, int userId);

    boolean deleteLike(int filmId, int userId);

    List<Integer> getPopularFilm(int topSize);
}
