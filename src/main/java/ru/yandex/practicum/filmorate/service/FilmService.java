package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.entity.Event;
import ru.yandex.practicum.filmorate.entity.EventOperation;
import ru.yandex.practicum.filmorate.entity.EventType;
import ru.yandex.practicum.filmorate.config.FilmorateApplicationSettings.DirectorSortParams;
import ru.yandex.practicum.filmorate.entity.Director;
import ru.yandex.practicum.filmorate.entity.Film;
import ru.yandex.practicum.filmorate.entity.Genre;
import ru.yandex.practicum.filmorate.entity.Mpa;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.EntityValidateException;
import ru.yandex.practicum.filmorate.exception.InternalServiceException;
import ru.yandex.practicum.filmorate.repository.EventRepository;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.RatingRepository;
import ru.yandex.practicum.filmorate.repository.UtilRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import static ru.yandex.practicum.filmorate.config.FilmorateApplicationSettings.*;

/**
 * Сервис содержит логику работы с пользователями
 */
@Log4j2
@Valid
@Service
@RequiredArgsConstructor
public class FilmService implements BaseFilmService {
    private final String thisService = this.getClass().getName();
    /**
     * Подключение репозитория для работы с фильмами.
     */
    private final FilmRepository films;
    /**
     * Подключение репозитория для работы с "лайками".
     */
    private final RatingRepository ratings;
    /**
     * Подключение репозитория для работы с сервисными запросами в репозиторий.
     */
    private final UtilRepository utils;

    private final EventRepository events;

    /**
     * Метод позволяет пользователю поставить 'лайк' фильму.
     *
     * @param filmId ID фильма
     * @param userId ID пользователя
     */
    @Override
    public void likeFilm(int filmId, int userId) {
        log.info("Добавление 'лайка' фильму на сервисе");
        ratings.likeFilm(filmId, userId);
        events.create(new Event(
                Instant.now().toEpochMilli(),
                userId, EventType.LIKE.toString(),
                EventOperation.ADD.toString(),
                filmId));
    }

    /**
     * Метод позволяет пользователю поставить 'дизлайк' фильму.
     *
     * @param filmId ID фильма
     * @param userId ID пользователя
     */
    @Override
    public void dislikeFilm(int filmId, int userId) {
        log.info("Добавление 'дизлайка' фильму на сервисе");
        ratings.dislikeFilm(filmId, userId);
        events.create(new Event(Instant.now().toEpochMilli(), userId, EventType.LIKE.toString(), EventOperation.REMOVE.toString(), filmId));
    }

    /**
     * Метод получает топ лучших фильмов по лайкам пользователей.
     *
     * @param topSize размер топа
     * @return топ лучших фильмов
     */
    @Override
    public List<Film> getTopFilms(Integer topSize, Integer genreId, Integer year) {
        log.info("Получение списка наиболее популярных фильмов по рейтингу пользователей, топ {}:", topSize);
        return films.getPopularFilm(topSize, genreId, year);
    }

    /**
     * Метод создает запись о фильме на сервисе.
     *
     * @param film фильм
     * @return этот же фильм с установленным ID после регистрации
     */
    @Override
    public Film createfilm(Film film) {
        log.info("Создание записи о фильме: {}", film);
        validateAndUpdateFilm(film);
        return films.createFilm(film).orElseThrow(
                () -> new InternalServiceException(thisService, films.getClass().getName(),
                        "Не удалось создать запись о фильме."));
    }

    /**
     * Метод обновляет запись о фильме на сервисе.
     *
     * @param film обновленная запись о фильме
     * @return обновленная запись о фильме
     */
    @Override
    public Film updateFilm(Film film) {
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
    public List<Film> getFilmsSortedByCriteria(int directorId, String criteria) {
        log.info("Получение списка всех фильмов сервиса, отобранных по критериям:");
        String condition;
        if (DirectorSortParams.year.toString().equals(criteria)) {
            condition = " order by FILM_RELEASE_DATE";
        } else if (DirectorSortParams.likes.toString().equals(criteria)) {
            condition = " order by FILM_RATING desc;";
        } else {
            throw new EntityValidateException(
                    thisService, "Валидация параметров запроса", "Этот функционал не реализован");
        }
        return films.findFilmsForDirectorByConditions(directorId, condition);
    }

    /**
     * Метод возвращает запись о конкретном фильме.
     *
     * @param id ID искомого фильма
     * @return найденная запись о фильме
     */
    @Override
    public Film getFilm(int id) {
        log.info("Получение с сервиса записи о фильме:");
        return films.getFilm(id).orElseThrow(() -> new EntityNotFoundException(
                thisService, films.getClass().getName(),
                String.format("Получить запись о фильме не удалось, фильм с ID %d не найден!", id)));
    }

    /**
     * Метод возвращает список общих с другом фильмов с сортировкой по их популярности
     *
     * @param userId   идентификатор пользователя, запрашивающего информацию
     * @param friendId идентификатор пользователя, с которым необходимо сравнить список фильмов
     * @return возвращает список фильмов, отсортированных по популярности.
     */
    @Override
    public List<Film> getCommonFilms(int userId, int friendId) {
        return films.getCommonFilms(userId, friendId);
    }

    @Override
    public void deleteFilm(int id) {
        films.deleteFilmById(id);
    }

    /**
     * Метод проверяет, что ID MPA-рейтинга, ID в списках жанров и режиссеров имеются в БД,
     * и присваивает соответствующие названия полям фильма по этим ID. В списках удаляются повторы.
     *
     * @param film фильм, в котором нужно проверить ID и присвоить полям названия
     */
    private void validateAndUpdateFilm(Film film) {
        film.setMpa(getMpa(film));
        film.setGenres(getGenres(film).stream().toList());
        film.setDirectors(getDirectors(film));
        film.setRate(DEFAULT_RATE);
    }

    private TreeSet<Genre> getGenres(Film film) {
        var genres = film.getGenres();
        var allGenres = utils.getAllGenres();
        var sortedGenres = new TreeSet<>(Genre::compareTo);
        if (genres != null && !genres.isEmpty()) {
            if (genres.stream().anyMatch(genre -> genre.getId() > allGenres.size())) {
                throw new EntityValidateException(thisService,
                        "Ошибка валидации параметров запроса", "ID жанра превышает число известных в БД");
            }
            genres.forEach(genre -> genre.setName(allGenres.get(genre.getId() - 1).getName()));
            sortedGenres.addAll(genres);
        }
        return sortedGenres;
    }

    private TreeSet<Director> getDirectors(Film film) {
        var directors = film.getDirectors();
        var allDirectors = utils.getAllDirectors();
        var sortedDirectors = new TreeSet<>(Director::compareTo);
        if (directors != null) {
            directors.forEach(director -> {
                var foundDirector = allDirectors
                        .stream()
                        .filter(d -> d.getId() == director.getId())
                        .findFirst()
                        .orElseThrow(() -> new EntityValidateException(thisService,
                                "Поиск режиссера ID " + director.getId(), "Режиссера нет в БД"));
                sortedDirectors.add(foundDirector);
            });
        }
        return sortedDirectors;
    }

    public Mpa getMpa(Film film) {
        var filmMpa = film.getMpa();
        var filmMpaId = DEFAULT_MPA_RATING;
        var allMpa = utils.getAllMpa();
        if (filmMpa == null) {
            filmMpa = new Mpa(filmMpaId, allMpa.getFirst().getName());
        } else {
            filmMpaId = filmMpa.getId();
            if (filmMpaId > allMpa.size()) {
                throw new EntityValidateException(thisService,
                        "Ошибка валидации параметров запроса", "ID MPA-рейтинга превышает число известных в БД");
            } else {
                filmMpa = new Mpa(filmMpaId, allMpa.get(filmMpaId - 1).getName());
            }
        }
        return filmMpa;
    }

    @Override
    public List<Film> getFilmsByTitleAndDirector(String query, String searchParameters) {
        log.info("Начали проверять вошедшие параметры {}", searchParameters);
        String director = "";
        String title = "";
        List<Film> filmsList = new ArrayList<>();
        //Проверяем 1 или 2 параметра пришло на вход(по наличию запятой)
        if (searchParameters.contains(",")) {
            log.info("Пришло 2 параметра на вход query =  {} ,  searchParameters = {}",
                    query, searchParameters);
            String[] params = searchParameters.split(",");
            for (String param : params) {
                if (!isInEnum(param)) {
                    throw new ValidationException("Такого параметра не существует " + param);
                }
            }
            director = query;
            title = query;
            filmsList.addAll(films.search(title, director));
        //Т.к. не сработало правило на 2 параметра через запятую - проверяем одинарный параметр фильтрации
        } else {
            log.info("Сработало правило фильтрации searchParameters без запятой, на вход пришло {}",
                    searchParameters);
            if (!isInEnum(searchParameters)) {
                throw new ValidationException("Параметра " + searchParameters + " не существует ");
            } else if (searchParameters.equals(By.DIRECTOR.toString().toLowerCase())) {
                director = query;
            } else if (searchParameters.equals(By.TITLE.toString().toLowerCase())) {
                title = query;
            }
            filmsList.addAll(films.search(title, director));
        }
        return filmsList;
    }

    enum By {
        DIRECTOR, TITLE
    }

    //Метод для проверки того, что пришедший параметр находится в списке разрешенных
    public static boolean isInEnum(String str) {
        By.valueOf(str.toUpperCase());
        return true;
    }
}