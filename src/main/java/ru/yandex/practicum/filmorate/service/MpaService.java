package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.entity.Mpa;
import ru.yandex.practicum.filmorate.repository.UtilRepository;

import java.util.List;

@Log4j2
@Valid
@Service
@RequiredArgsConstructor
public class MpaService implements BaseMpaService {
    /**
     * Подключение репозитория для работы с MPA-рейтингами.
     */
    private final UtilRepository util;

    /**
     * Метод возвращает MPA-рейтинг {@link Mpa} по указанному идентификатору
     *
     * @param mpaId идентификатор
     * @return {@link Mpa}
     */
    @Override
    public Mpa getMpa(int mpaId) {
        return util.getMpaById(mpaId);
    }

    /**
     * Метод возвращает список всех известных MPA-рейтингов {@link Mpa}
     *
     * @return список {@link Mpa}
     */
    @Override
    public List<Mpa> getAllMpa() {
        return util.getAllMpa();
    }
}
