package ru.yandex.practicum.filmorate.services.like;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.interfaces.LikesService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис реализует хранение и обработку в памяти списков "лайков" пользователей к фильмам.
 */
@Slf4j
@Component
public class InMemoryLikesService implements LikesService {
    /**
     * Список лайков каждого фильма.
     */
    private final Map<Integer, HashSet<Integer>> likes = new HashMap<>();
    private final Map<Integer, Integer> raiting
    /**
     * Метод регистрирует ID фильма в LikesService.
     *
     * @param filmId регистрируемый фильм
     * @return true - успешно, false - фильм уже зарегистрирован.
     */
    @Override
    public boolean registerFilm(int filmId) {
        if (likes.containsKey(filmId)) {
            log.warn("Фильм с ID {} уже был зарегистрирован!", filmId);
            return false;
        } else {
            likes.put(filmId, new HashSet<>());
            log.info("Фильм с ID {} зарегистрирован в LikesService", filmId);
            return true;
        }
    }

    /**
     * Метод удаляет ID фильма из LikesService, и его лайки у пользователей.
     *
     * @param filmId ID удаляемого фильма
     * @return true, если операция успешно выполнена, false - фильм не найден в LikesService
     */
    @Override
    public boolean unregisterFilm(int filmId) {
        if (likes.remove(filmId) != null) {
            log.info("Пользователь ID {} удален из LikesService", filmId);
            return true;
        } else {
            log.warn("Пользователь ID {} не найден в LikesService!", filmId);
            return false;
        }
    }

    /**
     * Метод реализует выставление пользователем лайка фильму.
     * @param filmId фильм
     * @param userId пользователь
     * @return true, если операция выполнена, false - если нет
     */
    @Override
    public boolean likeFilm(int filmId, int userId) {
        try {
            likes.get(filmId).add(userId);

            log.info("Пользователь ID {} поставил лайк фильму ID {}", userId, filmId);
            return true;
        } catch (NullPointerException e) {
            log.error("Фильм ID {} не зарегистрирован в LikesService!", filmId);
            return false;
        }
    }

    /**
     * Метод реализует удаление пользователем лайка фильму.
     *
     * @param filmId фильм
     * @param userId пользователь
     * @return true, если операция выполнена, false - если нет
     */
    @Override
    public boolean deleteLike(int filmId, int userId) {
        try {
            likes.get(filmId).remove(userId);
            log.info("Пользователь ID {} удалил лайк фильму ID {}", userId, filmId);
            return true;
        } catch (NullPointerException e) {
            log.error("Фильм ID {} не зарегистрирован в LikesService!", filmId);
            return false;
        }
    }

    /**
     * @return
     */
    @Override
    public List<Integer> getPopularFilm(int topSize) {
        LinkedList<Integer> raiting = likes.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toList(likes.get))

    }
}
