package ru.yandex.practicum.filmorate.services.like;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.interfaces.LikesService;

import java.util.*;

/**
 * Сервис реализует хранение и обработку в памяти списков "лайков" пользователей к фильмам.
 */
@Slf4j
@Component
public class InMemoryLikesService implements LikesService {
    private final Map<Integer, FilmRate> rating = new TreeMap<>();

    /**
     * Пользователь ставит лайк фильму.
     *
     * @param filmId фильм
     * @param userId пользователь
     */
    @Override
    public void likeFilm(int filmId, int userId) {
        FilmRate filmRate;
        if (!rating.containsKey(filmId)) {
            filmRate = new FilmRate(filmId);
        } else {
            filmRate = rating.get(filmId);
        }
        filmRate.addLike(userId);
        rating.put(filmId, filmRate);
    }

    /**
     * Пользователь удаляет лайк фильму.
     *
     * @param filmId фильм
     * @param userId пользователь
     */
    @Override
    public void deleteLike(int filmId, int userId) {
        rating.get(filmId).deleteLike(userId);
    }

    /**
     * @return
     */
    @Override
    public List<Integer> getPopularFilm(int topSize) {
        return List.

    }

    @Getter
    private final class FilmRate {
        private int rating;
        private final int filmId;
        private final Set<Integer> users = new HashSet<>();

        private FilmRate(int filmId) {
            this.filmId = filmId;
        }

        public void addLike(int userId) {
            users.add(userId);
            rating++;
        }

        public void deleteLike(int userId) {
            users.remove(userId);
            rating--;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FilmRate filmRate = (FilmRate) o;

            return rating == filmRate.rating;
        }

        @Override
        public int hashCode() {
            return filmId;
        }

    }
}
