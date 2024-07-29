package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.entity.Mpa;

import java.util.List;

public interface BaseMpaService {

    /**
     * Метод возвращает MPA-рейтинг {@link Mpa} по указанному идентификатору
     *
     * @param mpaId идентификатор
     * @return {@link Mpa}
     */
    Mpa getMpa(int mpaId);

    /**
     * Метод возвращает список всех известных MPA-рейтингов {@link Mpa}
     *
     * @return список {@link Mpa}
     */
    List<Mpa> getAllMpa();

}
