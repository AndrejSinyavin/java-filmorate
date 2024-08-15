package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.entity.Director;
import ru.yandex.practicum.filmorate.exception.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.EntityValidateException;
import ru.yandex.practicum.filmorate.repository.DirectorRepository;

import java.util.Collection;

@Log4j2
@Service
@AllArgsConstructor
public class DirectorService implements BaseDirectorService {
    private final String thisService = this.getClass().getName();
    private final DirectorRepository directorRepository;

    /**
     * Возвращает из БД список всех известных режиссеров
     *
     * @return список всех режиссеров
     */
    @Override
    public Collection<Director> getAllDirectors() {
        log.info("Получение списка всех режиссеров сервиса");
        return directorRepository.findAll();
    }

    /**
     * Возвращает из БД режиссера по его ID
     *
     * @param directorId ID режиссера
     * @return режиссер
     */
    @Override
    public Director getDirectorById(int directorId) {
        log.info("Чтение записи о режиссере из БД");
        return directorRepository.findById(directorId).orElseThrow(() -> new EntityNotFoundException(
                thisService, directorRepository.getClass().getName(),
                String.format("Получить запись не удалось, режиссер с ID %d не найден!", directorId))
        );
    }

    /**
     * Создает из БД новую запись о режиссере
     *
     * @param director сущность режиссер
     * @return эта же сущность с установленным Id
     */
    @Override
    public Director createDirector(Director director) {
        if (director.getName() == null || director.getName().isBlank()) {
            throw new EntityValidateException(thisService, "Создание режиссера", "Не задано ФИО режиссера");
        }
        return directorRepository.create(director).orElseThrow(() ->
                new EntityAlreadyExistsException(
                        thisService, "Создание режиссера", director.getName() + " уже существует"
                ));
    }

    /**
     * Измеряет в БД имеющегося режиссера
     *
     * @param director сущность режиссер с установленным ID
     * @return измененный режиссер с этим же ID
     */
    @Override
    public Director updateDirector(Director director) {
        return directorRepository.update(director).orElseThrow(() ->
                new EntityNotFoundException(
                        thisService, "Обновление режиссера: режиссера ", director.getName() + " не существует"
                ));
    }

    /**
     * Удаляет режиссера из БД
     *
     * @param directorId ID удаляемого режиссера
     */
    @Override
    public void deleteDirector(int directorId) {
        directorRepository.delete(directorId);
    }
}
