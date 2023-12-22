package ru.yandex.practicum.filmorate.interfaces;

import java.util.List;

public interface LikesService {

    void likeFilm(int filmId, int userId);

    void deleteLike(int filmId, int userId);

    List<Integer> getPopularFilm(int topSize);
}
