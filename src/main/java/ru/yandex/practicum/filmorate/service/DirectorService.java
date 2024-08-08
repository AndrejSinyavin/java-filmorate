package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.entity.Director;
import ru.yandex.practicum.filmorate.repository.DirectorRepository;

import java.util.List;

@Log4j2
@Valid
@Service
@AllArgsConstructor
public class DirectorService implements BaseDirectorService{
    private final String thisService = this.getClass().getName();
    private final String entityNullError = "Ошибка! сущность Film = null";
    private final String idError = "Ошибка! ID сущности может быть только положительным значением";
    private final DirectorRepository directorRepository;

    /**
     * Возвращает из БД список всех известных режиссеров
     *
     * @return список всех режиссеров
     */
    @Override
    public List<Director> getAllDirectors() {
        return List.of();
    }

    /**
     * Возвращает из БД режиссера по его ID
     *
     * @param directorId ID режиссера
     * @return режиссер
     */
    @Override
    public Director getDirectorById(@Positive(message = idError) int directorId) {
        return null;
    }

    /**
     * Создает из БД новую запись о режиссере
     *
     * @param director сущность режиссер
     * @return эта же сущность с установленным Id
     */
    @Override
    public Director createDirector(@NotNull(message = entityNullError) Director director) {
        return null;
    }

    /**
     * Измеряет в БД имеющегося режиссера
     *
     * @param director сущность режиссер с установленным ID
     * @return измененный режиссер с этим же ID
     */
    @Override
    public Director updateDirector(@NotNull(message = entityNullError) Director director) {
        return null;
    }

    /**
     * Удаляет режиссера из БД
     *
     * @param directorId ID удаляемого режиссера
     */
    @Override
    public void deleteDirector(@Positive(message = idError) int directorId) {

    }
}
