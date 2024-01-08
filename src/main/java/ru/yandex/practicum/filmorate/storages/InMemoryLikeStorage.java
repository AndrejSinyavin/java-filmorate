package ru.yandex.practicum.filmorate.storages;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.AppException;
import ru.yandex.practicum.filmorate.exceptions.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.*;

/**
 * Сервис реализует хранение и обработку в памяти рейтинга фильмов среди пользователей.
 */
@Slf4j
@Valid
@Component
public class InMemoryLikeStorage implements LikeStorage {
    private static final String ID_ERROR = "ID может быть только положительным значением";
    private static final String RATE_ERROR = "Рейтинг фильма не может быть отрицательным значением";
    private final String source = this.getClass().getSimpleName();
    // ключ - рейтинг, значение - список фильмов с этим рейтингом
    private final TreeMap<Integer, HashSet<FilmContext>> rating = new TreeMap<>(Comparator.reverseOrder());
    // ключ - id фильма, значение - контекст фильма
    private final HashMap<Integer, FilmContext> films = new HashMap<>();
    // ключ - id юзера, значение - контекст пользователя
    private final HashMap<Integer, UserContext> users = new HashMap<>();

    /**
     * Пользователь ставит лайк фильму.
     *
     * @param filmId фильм
     * @param userId пользователь
     * @return сформированное исключение, если лайк не поставлен; пустое значение, если операция выполнена успешно
     */
    @Override
    public Optional<? extends AppException> likeFilm(@Positive(message = ID_ERROR) int filmId,
                                                     @Positive(message = ID_ERROR)int userId) {
        String error = String.format("Пользователь ID %d ставит лайк фильму ID %d", userId, filmId);
        log.info(error);
        FilmContext film = films.get(filmId);
        if (film == null) {
            return Optional.of(new EntityNotFoundException(source, error, "Фильм не найден"));
        }
        UserContext user = users.get(userId);
        if (user == null) {
            return Optional.of(new EntityNotFoundException(source, error, "Пользователь не найден"));
        }
        if (user.whatLiked.contains(filmId)) {
            return Optional.of(new EntityAlreadyExistsException(
                    source, error, "Пользователь уже поставил лайк фильму"));
        }
        user.whatLiked.add(filmId);
        film.whoLiked.add(userId);
        int rate = film.rate;
        HashSet<FilmContext> members = rating.get(rate++);
        members.remove(film);
        film.rate++;
        rating.computeIfAbsent(rate, k -> new HashSet<>()).add(film);
        log.info("Лайк поставлен, фильм ID {}, рейтинг {}", filmId, film.rate);
        return Optional.empty();
    }

    /**
     * Пользователь отменяет лайк фильму.
     *
     * @param filmId фильм
     * @param userId пользователь
     * @return сформированное исключение, если лайк не поставлен; пустое значение, если операция выполнена успешно
     */
    @Override
    public Optional<? extends AppException> unlikeFilm(@Positive(message = ID_ERROR) int filmId,
                                                       @Positive(message = ID_ERROR)int userId) {
        String error = String.format("Пользователь ID %d отменяет лайк фильму ID %d", userId, filmId);
        log.info(error);
        FilmContext film = films.get(filmId);
        if (film == null) {
            return Optional.of(new EntityNotFoundException(source, error, "Фильм не найден"));
        }
        UserContext user = users.get(userId);
        if (user == null) {
            return Optional.of(new EntityNotFoundException(source, error, "Пользователь не найден"));
        }
        if (!user.whatLiked.contains(filmId)) {
            return Optional.of(new EntityNotFoundException(
                    source, error, "Пользователь не ставил лайк фильму"));
        }
        user.whatLiked.remove(filmId);
        film.whoLiked.remove(userId);
        int rate = film.rate;
        HashSet<FilmContext> members;
        if (rate != 0) {
            members = rating.get(rate--);
            members.remove(film);
            film.rate--;
        } else {
            members = rating.get(0);
            members.remove(film);
        }
        rating.computeIfAbsent(rate, k -> new HashSet<>()).add(film);
        if (members.isEmpty()) {
            rating.remove(++rate);
        }
        log.info("Лайк отменен, фильм ID {}, рейтинг {}", filmId, film.rate);
        return Optional.empty();
    }

    /**
     * Метод вызывается при создании фильма в фильмотеке. Регистрирует фильм в сервисе LikeStorage.
     *
     * @param filmId ID фильма
     * @param rate   рейтинг фильма
     * @return пустое значение, если регистрация выполнена; иначе - сформированное исключение с ошибкой
     */
    @Override
    public Optional<? extends AppException> registerFilm(@Positive(message = ID_ERROR) int filmId,
                                                         @PositiveOrZero(message = RATE_ERROR) int rate) {
        String error = String.format("Регистрация фильма ID %d в системе рейтинга", filmId);
        log.info(error);
        if (films.containsKey(filmId)) {
            return Optional.of(new EntityAlreadyExistsException(source, error, "Фильм уже зарегистрирован"));
        }
        var film = new FilmContext(filmId, rate, new HashSet<>());
        films.put(filmId, film);
        if (rating.containsKey(rate)) {
            rating.get(rate).add(film);
        } else {
            var members = new HashSet<FilmContext>();
            members.add(film);
            rating.put(rate, members);
        }
        log.info("Регистрация выполнена");
        return Optional.empty();
    }

    /**
     * Метод вызывается при обновлении фильма в фильмотеке.
     *
     * @param filmId ID фильма
     * @param rate   рейтинг фильма
     * @return пустое значение, если регистрация выполнена; иначе - сформированное исключение с ошибкой
     */
    @Override
    public Optional<? extends AppException> updateFilm(@Positive(message = ID_ERROR) int filmId,
                                                       @PositiveOrZero(message = RATE_ERROR) int rate) {
        String error = String.format("Обновление состояния фильма ID %d в системе рейтинга", filmId);
        log.info(error);
        if (!films.containsKey(filmId)) {
            return Optional.of(new EntityNotFoundException(source, error, "Фильм не найден"));
        }
        var film = films.get(filmId);
        var members = rating.get(film.rate);
        members.remove(film);
        film.rate = rate;
        if (rating.containsKey(rate)) {
            members = rating.get(rate);
            members.add(film);
        } else {
            members = new HashSet<>();
            members.add(film);
            rating.put(rate, members);
        }
        log.info("Обновление выполнено");
        return Optional.empty();
    }

    /**
     * Метод вызывается при удалении фильма из фильмотеки. Отменяет регистрацию фильма в сервисе LikeStorage.
     *
     * @param filmId ID фильма
     * @return пустое значение, если операция завершена успешно, иначе сообщение с ошибкой.
     */
    @Override
    public Optional<? extends AppException> unregisterFilm(@Positive(message = ID_ERROR) int filmId) {
        String error = String.format("Отмена регистрации фильма ID %d в системе рейтинга", filmId);
        log.info(error);
        FilmContext deleted = films.remove(filmId);
        if (deleted == null) {
            return Optional.of(new EntityNotFoundException(source, error, "Фильм не найден"));
        }
        int rate = deleted.rate;
        var members = rating.get(rate);
        members.remove(deleted);
        if (members.isEmpty()) {
            rating.remove(rate);
        }
        for (int id : deleted.whoLiked) {
            users.get(id).whatLiked.remove(filmId);
        }
        log.info("Отмена регистрации выполнена");
        return Optional.empty();
    }

    /**
     * Метод возвращает рейтинг фильма
     *
     * @param filmId ID фильма
     * @return рейтинг фильма, или пустое значение - если ошибка
     */
    @Override
    public Optional<Integer> getFilmRate(@Positive(message = ID_ERROR) int filmId) {
        String error = String.format("Получение рейтинга фильма ID %d", filmId);
        log.info(error);
        if (films.containsKey(filmId)) {
            int result = films.get(filmId).rate;
            log.info("Рейтинг = {}", result);
            return Optional.of(result);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Метод возвращает топ рейтинга фильмов по количеству лайков
     *
     * @param topSize размер топа
     * @return список ID фильмов топа в порядке убывания количества лайков
     */
    @Override
    public List<Integer> getPopularFilm(
            @PositiveOrZero(message = "Размер топа лучших фильмов не может быть отрицательным") int topSize) {
        String error = "Получение топа рейтинга";
        log.info(error);
        LinkedList<Integer> result = new LinkedList<>();
        if (rating.isEmpty()) {
            return result;
        }
        var key = rating.firstKey();
        HashSet<FilmContext> members = rating.get(key);
        while (true) {

            for (var context : members) {
                result.add(context.id);
                topSize--;
                if (topSize == 0) break;
            }
            if (topSize == 0) break;
            key = rating.higherKey(key);
            if (key == null) break;
            members = rating.get(key);
        }
        log.info(result.toString());
        return result;
    }

    /**
     * Метод вызывается при создании пользователя в фильмотеке. Регистрирует пользователя в LikeStorage.
     *
     * @param userId ID пользователя
     * @return пустое значение, если операция завершена успешно, иначе сообщение об ошибке
     */
    @Override
    public Optional<String> registerUser(int userId) {
        String error = String.format("Регистрация пользователя ID %d", userId);
        log.info(error);
        if (users.containsKey(userId)) {
            return Optional.of("Пользователь уже зарегистрирован в LikeStorage");
        }
        users.put(userId, new UserContext(userId, new HashSet<>()));
        log.info("Ок");
        return Optional.empty();
    }

    /**
     * Метод вызывается при удалении пользователя из фильмотеки. Отменяет регистрацию пользователя в LikeStorage.
     *
     * @param userId ID пользователя
     * @return пустое значение, если операция завершена успешно, иначе сообщение об ошибке
     */
    @Override
    public Optional<String> unregisterUser(int userId) {
        String error = String.format("Отмена регистрации пользователя ID %d", userId);
        log.info(error);
        if (!users.containsKey(userId)) {
            return Optional.of("Пользователь не зарегистрирован в LikeStorage");
        }
        var deletedUser = users.remove(userId);
        // ToDo: реализовать удаление лайков у фильмов и пересчет их рейтинга, если потребуется
        log.info("Ок");
        return Optional.empty();
    }

    @AllArgsConstructor
    private static class FilmContext {
        private int id;
        private int rate;
        private HashSet<Integer> whoLiked;
    }

    @AllArgsConstructor
    private static class UserContext {
        int id;
        HashSet<Integer> whatLiked;
    }
}
