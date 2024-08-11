package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.entity.Director;

import java.util.Collection;
import java.util.Optional;

/**
 * Интерфейс репозиториев для работы с режиссерами
 */
public interface DirectorRepository {
    /**
     * Возвращает список всех режиссеров
     *
     * @return список режиссеров
     */
    Collection<Director> findAll();

    /**
     * Возвращает режиссера по его ID
     *
     * @param id режиссера
     * @return режиссер, или пустое значение, если не найден
     */
    Optional<Director> findById(int id);

    /**
     * Создает режиссера
     *
     * @param director режиссер, которого нужно создать
     * @return он же с установленным ID, или пустое значение, если не получилось
     */
    Optional<Director> create(Director director);

    /**
     * Обновление существующего режиссера
     *
     * @param director режиссер с целевым ID
     * @return он же, или пустое значение, если не получилось
     */
    Optional<Director> update(Director director);

    /**
     * Удаление режиссера
     *
     * @param id режиссера
     */
    void delete(int id);
}
