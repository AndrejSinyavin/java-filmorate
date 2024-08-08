package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.config.FilmorateApplicationSettings.DirectorSortParams;
import ru.yandex.practicum.filmorate.entity.Director;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.entity.Genre;
import ru.yandex.practicum.filmorate.entity.Mpa;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.EntityValidateException;
import ru.yandex.practicum.filmorate.exception.InternalServiceException;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.LikeRepository;
import ru.yandex.practicum.filmorate.repository.UtilRepository;

import java.util.List;
import java.util.TreeSet;

import static ru.yandex.practicum.filmorate.config.FilmorateApplicationSettings.DEFAULT_MPA_RATING;

/**
 * Сервис содержит логику работы с пользователями
 */
@Log4j2
@Valid
@Service
@RequiredArgsConstructor
public class FilmService implements BaseFilmService {
    private final String thisService = this.getClass().getName();
    private final String entityNullError = "Ошибка! сущность Film = null";
    private final String idError = "Ошибка! ID сущности может быть только положительным значением";
    /**
     * Подключение репозитория для работы с фильмами.
     */
    private final FilmRepository films;
    /**
     * Подключение репозитория для работы с "лайками".
     */
    private final LikeRepository likes;
    private final UtilRepository utils;

    /**
     * Метод позволяет пользователю лайкнуть фильм.
     *
     * @param filmId ID фильма
     * @param userId ID пользователя
     */
    @Override
    public void addLike(@Positive(message = idError) int filmId,
                        @Positive(message = idError) int userId) {
        log.info("Добавление лайка фильму на сервисе");
        likes.likeFilm(filmId, userId);
    }

    /**
     * Метод позволяет пользователю удалить ранее поставленный лайк фильму.
     *
     * @param filmId ID фильма
     * @param userId ID пользователя
     */
    @Override
    public void deleteLike(@Positive(message = idError) int filmId,
                           @Positive(message = idError) int userId) {
        log.info("Удаление лайка фильму на сервисе:");
        likes.unLikeFilm(filmId, userId);
    }

    /**
     * Метод получает топ лучших фильмов по лайкам пользователей.
     *
     * @param topSize размер топа
     * @return топ лучших фильмов
     */
    @Override
    public List<Film> getTopFilms(@Positive(message = idError) int topSize) {
        log.info("Получение списка наиболее популярных фильмов по количеству лайков, топ {}:", topSize);
        return films.getPopularFilm(topSize);
    }

    /**
     * Метод создает запись о фильме на сервисе.
     *
     * @param film фильм
     * @return этот же фильм с установленным ID после регистрации
     */
    @Override
    public Film createfilm(@NotNull(message = entityNullError) Film film) {
        log.info("Создание записи о фильме: {}", film);
        validateAndUpdateFilm(film);
        return films.createFilm(film).orElseThrow(
                () -> new InternalServiceException(thisService, films.getClass().getName(),
                        "Не удалось создать запись на сервисе."));
    }

    /**
     * Метод обновляет запись о фильме на сервисе.
     *
     * @param film обновленная запись о фильме
     * @return обновленная запись о фильме
     */
    @Override
    public Film updateFilm(@NotNull(message = entityNullError) Film film) {
        log.info("Обновление записи о фильме на сервисе: {}", film);
        validateAndUpdateFilm(film);
        return films.updateFilm(film).orElseThrow(
                () -> new EntityNotFoundException(thisService, films.getClass().getName(),
                        "Обновить запись о фильме не удалось, запись не найдена на сервисе."));
    }

    /**
     * Метод возвращает список всех записей о фильмах.
     *
     * @return список фильмов
     */
    @Override
    public List<Film> getFilms() {
        log.info("Получение списка всех фильмов сервиса:");
        return films.getFilms();
    }

    /**
     * Возвращает из БД список фильмов режиссера, отсортированный по заданному критерию.
     *
     * @param directorId режиссер
     * @param criteria   критерий сортировки
     * @return список фильмов этого режиссера, отсортированный по критерию
     */
    @Override
    public List<Film> getFilmsSortedByCriteria(
            @Positive(message = idError) int directorId,
            @NotBlank(message = "Ошибка! Отсутствует критерий сортировки")
            String criteria) {
        log.info("Получение списка всех фильмов сервиса, отобранных по критериям:");
        String conditions;
        if (DirectorSortParams.year.toString().equals(criteria)) {
            conditions = " order by FILM_RELEASE_DATE";
        } else if (DirectorSortParams.likes.toString().equals(criteria)) {
            conditions = " order by RATE;";
        } else {
            throw new EntityValidateException(
                    thisService,"Валидация параметров запроса", "Этот функционал не реализован");
        }
        return films.findFilmsForDirectorByConditions(directorId, conditions);
    }

    /**
     * Метод возвращает запись о конкретном фильме.
     *
     * @param id ID искомого фильма
     * @return найденная запись о фильме
     */
    @Override
    public Film getFilm(@Positive(message = idError) int id) {
        log.info("Получение с сервиса записи о фильме:");
        return films.getFilm(id).orElseThrow(() -> new EntityNotFoundException(
                thisService, films.getClass().getName(),
                String.format("Получить запись о фильме не удалось, фильм с ID %d не найден!", id)));
    }

    /**
     * Метод проверяет, что ID MPA-рейтинга, ID в списках жанров и режиссеров имеются в БД,
     * и присваивает соответствующие названия полям фильма по этим ID. В списках удаляются повторы.
     *
     * @param film фильм, в котором нужно проверить ID и присвоить полям названия
     */
    private void validateAndUpdateFilm(@NotNull(message = entityNullError) Film film) {
        film.setMpa(getMpa(film));
        var genres = film.getGenres();
        var allGenres = utils.getAllGenres();
        var sortedGenres = new TreeSet<>(Genre::compareTo);
        if (genres != null && !genres.isEmpty()) {
            if (genres.stream().anyMatch(genre -> genre.getId() > allGenres.size())) {
                throw new EntityValidateException(thisService,
                        "Ошибка валидации параметров запроса", "ID жанра превышает число известных в БД");
            }
            genres.forEach(genre -> genre.setName(allGenres.get(genre.getId()-1).getName()));
            sortedGenres.addAll(genres);
        }
        film.setGenres(sortedGenres.stream().toList());
        var directors = film.getDirector();
        var allDirectors = utils.getAllDirectors();
        var sortedDirectors = new TreeSet<>(Director::compareTo);
        if (directors != null && !directors.isEmpty()) {
            if (directors.stream().anyMatch(director -> director.getId() > allDirectors.size())) {
                throw new EntityValidateException(thisService,
                        "Ошибка валидации параметров запроса", "ID директора превышает число известных в БД");
            }
            directors.forEach(director ->
                    director.setName(allDirectors.get(director.getId()-1).getName()));
            sortedDirectors.addAll(directors);
        }
        film.setDirector(sortedDirectors.stream().toList());
    }

    public Mpa getMpa(@NotNull(message = entityNullError) Film film) {
        var mpa = film.getMpa();
        var mpaId = DEFAULT_MPA_RATING;
        var mpas = utils.getAllMpa();
        if (mpa == null) {
            mpa = new Mpa(mpaId, mpas.getFirst().getName());
        } else {
            mpaId = mpa.getId();
            if (mpaId > mpas.size()) {
                throw new EntityValidateException(thisService,
                        "Ошибка валидации параметров запроса", "ID MPA-рейтинга превышает число известных в БД");
            } else {
                mpa = new Mpa(mpaId, mpas.get(mpaId - 1).getName());
            }
        }
        return mpa;
    }
}
