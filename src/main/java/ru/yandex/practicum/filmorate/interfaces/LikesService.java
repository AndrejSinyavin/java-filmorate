package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.models.Film;

import java.util.List;

public interface LikesService {
    void likeMovie(int filmId, int userId);

    void deleteLike(int filmId, int userId);

    List<Film> getPopularFilm();
}
