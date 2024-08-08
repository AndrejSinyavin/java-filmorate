package ru.yandex.practicum.filmorate.service;

import jakarta.validation.constraints.NotNull;
import ru.yandex.practicum.filmorate.config.FilmorateApplicationSettings.DirectorSortParams;
import ru.yandex.practicum.filmorate.entity.Director;
import ru.yandex.practicum.filmorate.entity.Film;

import java.util.List;

public interface BaseDirectorService {

    /**
     * Возвращает список всех известных режиссеров
     *
     * @return список всех режиссеров
     */
    List<Director> getAllDirectors();

    /**
     * Возвращает режиссера по его ID
     *
     * @param directorId ID режиссера
     * @return режиссер
     */
    Director getDirectorById(int directorId);

    /**
     * Создает новую запись о режиссере
     *
     * @param name сущность режиссер
     * @return эта же сущность с установленным Id
     */
    Director createDirector(String name);


    /**
     * Измеряет имеющегося режиссера
     *
     * @param director сущность режиссер с установленным ID
     * @return измененный режиссер с этим же ID
     */
    Director updateDirector(Director director);

    /**
     * Удаляет режиссера
     *
     * @param directorId ID удаляемого режиссера
     */
    void deleteDirector(int directorId);
}
