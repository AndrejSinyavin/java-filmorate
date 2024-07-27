package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.entity.Genre;

import java.util.List;

/**
 * Сервис содержит логику работы с пользователями
 */
@Log4j2
@Valid
@Service
@AllArgsConstructor
public class GenreService implements BaseGenreService {
    private final String idError = "Ошибка! ID может быть только положительным значением";
    /**
     * Подключение сервиса работы со служебным сервисом.
     */
    private final BaseUtilityService service;


    /**
     * Метод возвращает ID жанра и его имя
     *
     * @param id искомый жанр
     * @return ID жанра и его имя
     */
    @Override
    public Genre getGenre(int id) {
        log.info("Получение названия жанра и его ID ");
        return service.getGenre(id);
    }

    /**
     * Метод возвращает список из ID жанра и его имени для всех жанров
     *
     * @return список из ID жанра и его имени для всех жанров
     */
    @Override
    public List<Genre> getAllGenres() {
        log.info("Получение списка всех имеющихся жанров фильмов");
        return service.getGenresFromDb();
    }
}
