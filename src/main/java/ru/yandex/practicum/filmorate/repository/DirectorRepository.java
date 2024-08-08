package ru.yandex.practicum.filmorate.repository;

import jakarta.validation.constraints.NotNull;
import ru.yandex.practicum.filmorate.entity.Director;
import ru.yandex.practicum.filmorate.entity.Film;

import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для репозиториев для работы с режиссерами
 */
public interface DirectorRepository {
    /**
     * Возвращает список всех режиссеров
     *
     * @return список режиссеров
     */
    List<Director> findAll();

    /**
     * Возвращает режиссера по его ID
     *
     * @param id режиссера
     * @return режиссер, или пустое значение, если не найден
     */
    Optional<Director> findById(int id);

    /**
     * Создает режиссера в репозитории
     *
     * @param name режиссер, которого нужно создать
     * @return он же с установленным ID, или пустое значение, если не получилось
     */
    Optional<Director> create(String name);

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
