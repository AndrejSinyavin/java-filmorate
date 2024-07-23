package ru.yandex.practicum.filmorate.repository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.AppException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
@Valid
public class JdbcLikeRepository implements LikeRepository {
    /**
     * Пользователь ставит лайк фильму.
     *
     * @param filmId фильм
     * @param userId пользователь
     * @return сформированное исключение, если лайк не поставлен; пустое значение, если операция выполнена успешно
     */
    @Override
    public Optional<? extends AppException> likeFilm(int filmId, int userId) {
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
    public Optional<? extends AppException> unlikeFilm(int filmId, int userId) {
        return Optional.empty();
    }

    /**
     * Метод вызывается при создании фильма в фильмотеке. Регистрирует фильм в сервисе LikeRepository.
     *
     * @param filmId ID фильма
     * @param rate   рейтинг фильма
     * @return рейтинг фильма, или пустое значение - если ошибка
     */
    @Override
    public Optional<? extends AppException> registerFilm(int filmId, int rate) {
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
    public Optional<? extends AppException> updateFilm(int filmId, int rate) {
        return Optional.empty();
    }

    /**
     * Метод возвращает рейтинг фильма
     *
     * @param filmId ID фильма
     * @return пустое значение, если операция завершена успешно, иначе сформированное исключение
     */
    @Override
    public Optional<Integer> getFilmRate(int filmId) {
        return Optional.empty();
    }

    /**
     * Метод возвращает топ рейтинга фильмов по количеству лайков
     *
     * @param topSize размер топа
     * @return список ID фильмов топа в порядке убывания количества лайков
     */
    @Override
    public List<Integer> getPopularFilm(int topSize) {
        return List.of();
    }

    /**
     * Метод вызывается при создании пользователя в фильмотеке. Регистрирует пользователя в LikeRepository.
     *
     * @param userId ID пользователя
     * @return пустое значение, если операция завершена успешно, иначе сообщение об ошибке
     */
    @Override
    public Optional<String> registerUser(int userId) {
        return Optional.empty();
    }

}
