package ru.yandex.practicum.filmorate.repository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.Film;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
@Valid
public class JdbcFilmRepository implements FilmRepository {
    /**
     * Метод создает запись о фильме в фильмотеке.
     *
     * @param film запись о фильме, которую нужно создать в фильмотеке
     * @return этот же фильм с уже зарегистрированным ID в фильмотеке, или пустое значение при ошибке
     */
    @Override
    public Optional<Film> createfilm(Film film) {
        return Optional.empty();
    }

    /**
     * Метод обновляет существующую запись о фильме в фильмотеке.
     *
     * @param film запись о фильме из запроса с установленным ID, по которому ищется этот фильм в фильмотеке.
     * @return обновленная запись о фильме, или пустое значение при ошибке
     */
    @Override
    public Optional<Film> updateFilm(Film film) {
        return Optional.empty();
    }

    /**
     * Метод возвращает список всех записей о фильмах в фильмотеке.
     *
     * @return список фильмов, может быть пустым
     */
    @Override
    public List<Film> getFilms() {
        return List.of();
    }

    /**
     * Метод возвращает запись о фильме по его ID.
     *
     * @param filmId ID искомого фильма
     * @return запись о фильме; либо пустое значение, если запись о фильме не найдена в хранилище
     */
    @Override
    public Optional<Film> getFilm(int filmId) {
        return Optional.empty();
    }
}
