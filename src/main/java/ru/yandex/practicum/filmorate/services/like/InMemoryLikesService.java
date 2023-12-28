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
    private static final String ID_ERROR = "ID может быть только положительным значением";
    private static final String RATE_ERROR = "Рейтинг фильма не может быть отрицательным значением";
    /**
     * Хранилище записей ID фильмов и ID тех, кто поставил лайк.
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
     * @return новый рейтинг фильма, либо пустое значение, если лайк не поставлен
     */
    @Override
    public Optional<Integer> likeFilm(@Positive(message = ID_ERROR) int filmId,
                                      @Positive(message = ID_ERROR) int userId) {
        Node node = storage.get(filmId);
        if (Objects.isNull(node)) {
            node = new Node(filmId);
        } else if (node.whoLikedIt.contains(userId)) {
            log.warn("Пользователь ID {} уже ставил лайк фильму", userId);
            return Optional.empty();
        }
        top.remove(node);
        int rate = node.like(userId);
        storage.put(filmId, node);
        top.add(node);
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
        return Optional.of(rate);
    }

    /**
     * Пользователь отменяет лайк фильму.
     *
     * @param filmId фильм
     * @param userId пользователь
     * @return Optional<новый рейтинг>, или Optional.empty - если лайк поставить не удалось
     */
    @Override
    public Optional<Integer> unlikeFilm(@Positive(message = ID_ERROR) int filmId,
                                        @Positive(message = ID_ERROR) int userId) {
        Node node = storage.get(filmId);
        if (!Objects.isNull(node)) {
            if (!node.whoLikedIt.contains(userId)) {
                log.warn("Пользователь ID {} не ставил лайк фильму", userId);
                return Optional.empty();
            }
            top.remove(node);
            int rate = node.disLike(userId);
            if (rate == 0) {
                storage.remove(filmId, node);
                return Optional.of(rate);
            }
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
    public Optional<List<Integer>> getPopularFilm(
            @Positive(message = "Размер топа лучших фильмов не может быть меньше одного фильма") int topSize) {
        LinkedList<Integer> result = new LinkedList<>();
        for (Node node : top) {
            result.add(node.getFilmId());
            topSize--;
            if (topSize < 1) break;
        }
        log.info("Фильмов c рейтингом в фильмотеке: {}. Получен топ из {} фильмов: ID {}",
                storage.size(), result.size(), result);
        return Optional.of(result);
    }

    /**
     * Метод удаляет запись о лайках для фильма
     *
     * @param filmId ID фильма
     * @return пустое значение, если операция завершена успешно, иначе сообщение с ошибкой.
     */
    @Override
    public Optional<String> deleteFilmRate(@Positive(message = ID_ERROR) int filmId) {
        Node deleted = storage.remove(filmId);
        if (Objects.isNull(deleted)) {
            String errorMessage = String.format("Информация о фильме ID %d не найдена!", filmId);
            log.warn(errorMessage);
            return Optional.of(errorMessage);
        } else {
            top.remove(deleted);
            log.info("Информация о фильме удалена из LikesService");
            return Optional.empty();
        }
    }

    /**
     * Метод возвращает рейтинг фильма
     *
     * @param filmId ID фильма
     * @return рейтинг фильма
     */
    @Override
    public Optional<Integer> getFilmRate(@Positive(message = ID_ERROR) int filmId) {
        Node film = storage.get(filmId);
        if (Objects.isNull(film)) {
            String errorMessage = String.format("Информация о фильме ID %d не найдена!", filmId);
            log.warn(errorMessage);
            return Optional.empty();
        }
        return Optional.of(film.getRate());
    }

    /**
     * Метод вызывается при создании фильма в фильмотеке.
     *
     * @param filmId ID фильма
     * @param rate рейтинг фильма
     * @return пустое значение, если регистрация выполнена; иначе - сообщение с ошибкой
     */
    @Override
    public Optional<String> createFilmRate(
            @Positive(message = ID_ERROR) int filmId,
            @PositiveOrZero(message = RATE_ERROR) int rate) {
        Node node = storage.get(filmId);
        if (Objects.isNull(node)) {
            log.info("Регистрация фильма ID {}", filmId);
            node = new Node(filmId);
            if (LIKE_PROTECTED_MODE) {
                rate = 0;
            } else {
                for (int i = 1; i <= rate; i++) {
                    node.whoLikedIt.add(0);
                }
            }
            node.rate = rate;
            storage.put(filmId, node);
            top.add(node);
            return Optional.empty();
        } else {
            String errorMessage = "Фильм с этим ID уже зарегистрирован";
            log.warn(errorMessage);
            return Optional.of(errorMessage);
        }
    }

    /**
     * Метод вызывается при обновлении фильма в фильмотеке.
     *
     * @param filmId ID фильма
     * @param rate рейтинг фильма
     * @return пустое значение, если регистрация выполнена; иначе - сообщение с ошибкой
     */
    @Override
    public Optional<String> updateFilmRate(
            @Positive(message = ID_ERROR) int filmId,
            @PositiveOrZero(message = RATE_ERROR) int rate) {
        Node node = storage.get(filmId);
        if (!Objects.isNull(node)) {
            log.info("Обновление рейтинга фильма ID {}", filmId);
            top.remove(node);
            if (LIKE_PROTECTED_MODE) {
                rate = 0;
            }
            node.rate = rate;
            top.add(node);
            return Optional.empty();
        } else {
            String errorMessage = "Фильм с этим ID не зарегистрирован";
            log.warn(errorMessage);
            return Optional.of(errorMessage);
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
