package ru.yandex.practicum.filmorate.services.like;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.interfaces.LikesService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.*;

import static ru.yandex.practicum.filmorate.services.misc.ApplicationSettings.LIKE_PROTECTED_MODE;

/**
 * Сервис реализует хранение и обработку в памяти рейтинга фильмов среди пользователей.
 */
@Slf4j
@Component
@Valid
public class InMemoryLikesService implements LikesService {
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
     * @return Optional<новый рейтинг>
     */
    @Override
    public Optional<Integer> likeFilm(@Positive int filmId, @Positive int userId) {
        Node node = storage.get(filmId);
        if (Objects.isNull(node)) {
            node = new Node(filmId);
        } else {
            top.remove(node);
        }
        int rate = node.like(userId);
        storage.put(filmId, node);
        top.add(node);
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
        return Optional.of(rate);
    }

    /**
     * Пользователь удаляет лайк фильму.
     *
     * @param filmId фильм
     * @param userId пользователь
     * @return Optional<новый рейтинг>, или Optional.empty - если фильм не найден
     */
    @Override
    public Optional<Integer> disLikeFilm(@Positive int filmId, @Positive int userId) {
        Node node = storage.get(filmId);
        if (!Objects.isNull(node)) {
            top.remove(node);
            Integer rate = node.disLike(userId);
            top.add(node);
            log.info("Пользователь {} удалил лайк фильму {}", userId, filmId);
            return Optional.of(rate);
        } else {
            log.warn("В службе LikesService отсутствует информация о фильме! {}", filmId);
            return Optional.empty();
        }
    }

    /**
     * Метод возвращает топ рейтинга фильмов по количеству лайков
     *
     * @return список ID фильмов
     */
    @Override
    public List<Integer> getPopularFilm(@Positive int topSize) {
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
    public void deleteFilm(@Positive int filmId) {
        storage.remove(filmId);
        log.info("Информация о фильме удалена из LikesService");
    }

    /**
     * Метод возвращает рейтинг фильма
     *
     * @param filmId ID фильма
     * @return рейтинг фильма
     */
    @Override
    public int unregisterFilm(@Positive int filmId) {
        Node film = storage.get(filmId);
        int rate = 0;
        if (!Objects.isNull(film)) {
            rate = film.getRate();
            if (LIKE_PROTECTED_MODE && film.getWhoLikedIt().size() != film.getRate()) {
                return 0;
            }
        }
        return rate;
    }

    @Override
    public void registerFilm(@Positive int filmId, @PositiveOrZero int rate) {
        Node node = storage.get(filmId);
        if (Objects.isNull(node)) {
            node = new Node(filmId);
            storage.put(filmId, node);
            if (LIKE_PROTECTED_MODE) {
                top.remove(node);
                rate = 0;
            }
            node.rate = rate;
            top.add(node);
        }

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

        public int like(@Positive int userId) {
            if (whoLikedIt.add(userId)) {
                rate++;
            }
            return rate;
        }

        public int disLike(@Positive int userId) {
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
