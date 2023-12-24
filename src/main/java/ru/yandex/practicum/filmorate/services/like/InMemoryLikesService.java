package ru.yandex.practicum.filmorate.services.like;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.interfaces.LikesService;

import java.util.*;

/**
 * Сервис реализует хранение и обработку в памяти рейтинга фильмов среди пользователей.
 */
@Slf4j
@Component
public final class InMemoryLikesService implements LikesService {
    private static final String LIKES_SERVICE_INTERNAL_ERROR =
            "Выполнение операций с лайками в InMemoryLikesService";
    /**
     * Хранилище записей ID фильмов и ID тех, кто его "лайкнул".
     */
    private final Map<Integer, Node> storage = new HashMap<>();
    /**
     * Отсортированный по убыванию рейтинга список ID фильмов с лайками.
     */
    private final TreeSet<Node> top = new TreeSet<>(Comparator.comparingInt(Node::getRate));

    /**
     * Пользователь ставит лайк фильму.
     *
     * @param filmId фильм
     * @param userId пользователь
     * @return новый рейтинг фильма
     */
    @Override
    public int likeFilm(int filmId, int userId) {
        Node node = storage.get(filmId);
        if (node == null) {
            node = new Node(filmId);
        } else {
            top.remove(node);
        }
        int rate = node.like(userId);
        storage.put(filmId, node);
        top.add(node);
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
        return rate;
    }

    /**
     * Пользователь удаляет лайк фильму.
     *
     * @param filmId фильм
     * @param userId пользователь
     * @return новый рейтинг фильма
     */
    @Override
    public int disLikeFilm(int filmId, int userId) {
        Node node = storage.get(filmId);
        if (node != null) {
            top.remove(node);
            int rate = node.disLike(userId);
            top.add(node);
            log.info("Пользователь {} удалил лайк фильму {}", userId, filmId);
            return rate;
        } else {
            log.warn("Фильм не найден! {}", filmId);
            throw new EntityNotFoundException(this.getClass().getName(), LIKES_SERVICE_INTERNAL_ERROR,
                    "Не возможно удалить лайк, фильм не найден");
        }
    }

    /**
     * Метод возвращает топ рейтинга фильмов по количеству лайков
     *
     * @return список ID фильмов
     */
    @Override
    public List<Integer> getPopularFilm(int topSize) {
        LinkedList<Integer> result = new LinkedList<>();
        for (Node node : top) {
            result.add(node.getFilmId());
            topSize--;
            if (topSize < 1) break;
        }
        log.info("Фильмов c рейтингом в фильмотеке: {}. Получен топ из {} фильмов: ID {}",
                storage.size(), result.size(), result);
        return result;
    }

    /**
     * Метод удаляет запись о лайках для фильма
     *
     * @param filmId ID фильма
     */
    @Override
    public void deleteFilm(int filmId) {
        Node deletedNode = storage.remove(filmId);
        if (deletedNode == null) {
            log.warn("Удаление не возможно! Фильм с ID {} не найден", filmId);
        } else {
            top.remove(deletedNode);
            log.info("Информация о фильме удалена из LikesService");
        }
    }

    /**
     * Метод возвращает рейтинг фильма
     *
     * @param filmId ID фильма
     * @return рейтинг фильма
     */
    @Override
    public int getFilmRating(int filmId) {
        Node film = storage.get(filmId);
        int rate = 0;
        if (film != null) {
            rate = film.getRate();
        }
        return rate;
    }

    @Override
    public void setFilmRating(int filmId, int rate) {
        Node node = storage.get(filmId);
        if (node == null) {
            node = new Node(filmId);
            storage.put(filmId, node);
        }
        top.remove(node);
        node.rate = rate;
        top.add(node);
    }

    /**
     * Элемент хранилища сервиса лайков
     */
    @RequiredArgsConstructor
    private static final class Node {
        @Getter
        private final int filmId;
        private final Set<Integer> whoLikedIt = new HashSet<>();
        @Getter
        private int rate;

        public int like(int userId) {
            if (whoLikedIt.add(userId)) {
                rate++;
            }
            return rate;
        }

        public int disLike(int userId) {
            if (whoLikedIt.remove(userId)) {
                rate--;
            }
            return rate;
        }

        public List<Integer> getWhoLikedIt() {
            return new ArrayList<>(whoLikedIt);
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            Node node = (Node) object;
            return node.rate == rate;
        }

        @Override
        public int hashCode() {
            return Objects.hash(filmId);
        }
    }
}
