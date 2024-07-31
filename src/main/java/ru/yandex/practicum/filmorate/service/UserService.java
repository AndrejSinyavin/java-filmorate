package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.entity.User;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.InternalServiceException;
import ru.yandex.practicum.filmorate.repository.FriendRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.List;

/**
 * Сервис содержит логику работы с пользователями
 */
@Log4j2
@Valid
@Service
@AllArgsConstructor
public class UserService implements BaseUserService {
    private final String thisService = this.getClass().getName();
    /**
     * Подключение репозитория для работы с пользователями.
     */
    private final UserRepository users;
    /**
     * Подключение репозитория для работы с друзьями.
     */
    private final FriendRepository friends;

    /**
     * Метод создает запрос на дружбу, или подтверждает уже имеющийся запрос.
     *
     * @param userId   ID пользователя, создающего запрос
     * @param friendId ID пользователя, к которому добавляются
     */
    @Override
    public void addFriend(int userId, int friendId) {
        log.info("Запрос/подтверждение дружбы пользователей {} и {}", userId, friendId);
        friends.addFriend(userId, friendId);
    }

    /**
     * Метод удаляет имеющийся запрос/подтверждение дружбы.
     *
     * @param userId   ID пользователя
     * @param friendId ID друга пользователя
     */
    @Override
    public void deleteFriend(int userId, int friendId) {
        log.info("Удаление запроса/подтверждения дружбы пользователей {} и {}", userId, friendId);
        friends.deleteFriend(userId, friendId);
    }

    /**
     * Метод возвращает список друзей указанного пользователя.
     *
     * @param id ID нужного пользователя
     * @return список его друзей (может быть пустым, если нет друзей, отправивших встречный запрос/подтверждение)
     */
    @Override
    public List<User> getFriends(int id) {
        log.info("Получение списка друзей пользователя");
        return friends.getFriends(id);
    }

    /**
     * Метод возвращает список общих друзей двух пользователей
     *
     * @param userId   ID первого пользователя
     * @param friendId ID второго пользователя
     * @return список общих друзей, может быть пустым
     */
    @Override
    public List<User> getCommonFriends(int userId, int friendId) {
        log.info("Получение списка общих друзей двух пользователей:");
        return friends.getCommonFriends(userId, friendId);
    }

    /**
     * Метод создает на сервисе нового пользователя с уникальным ID.
     *
     * @param user создаваемый пользователь
     * @return этот же пользователь с уникальным ID
     */
    @Override
    public User createUser(User user) {
        log.info("Создание аккаунта пользователя на сервисе {}", user);
        return users.createUser(user).orElseThrow(() -> new InternalServiceException(
                thisService, users.getClass().getName(), "Ошибка при создании аккаунта пользователя сервиса!"));
    }

    /**
     * Метод обновляет в списке пользователей фильмотеки существующего пользователя.
     *
     * @param user пользователь, которого нужно найти и обновить, поиск производится по ID
     * @return обновленный пользователь
     */
    @Override
    public User updateUser(User user) {
        log.info("Обновление аккаунта о пользователе {}", user);
        return users.updateUser(user).orElseThrow(() -> new InternalServiceException(
                thisService, users.getClass().getName(), "Ошибка при обновлении аккаунта пользователя сервиса!"));
    }

    /**
     * Метод возвращает список всех пользователей фильмотеки.
     *
     * @return список всех пользователей, может быть пустым
     */
    @Override
    public List<User> getAllUsers() {
        log.info("Получение списка всех аккаунтов пользователей");
        return users.getAllUsers();
    }

    /**
     * Метод возвращает пользователя по его ID
     *
     * @param userId ID пользователя
     * @return искомый пользователь
     */
    @Override
    public User getUser(int userId) {
        log.info("Получение аккаунта пользователя ID {}", userId);
        return users.getUser(userId).orElseThrow(() -> new EntityNotFoundException(
                thisService, users.getClass().getName(),
                String.format("Пользователь ID %d не найден на сервере", userId)));
    }
}
