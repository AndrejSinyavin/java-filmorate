package ru.yandex.practicum.filmorate.storages.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.interfaces.RegistrationService;
import ru.yandex.practicum.filmorate.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.models.User;

import java.util.*;

/**
 * Хранилище и бизнес-логика работы со списком клиентов фильмотеки в памяти.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private static final String USER_STORAGE_INTERNAL_ERROR =
            "Выполнение операций с пользователями в хранилище в памяти";
    /**
     * Хранилище пользователей фильмотеки в памяти.
     */
    private final Map<Integer, User> users = new HashMap<>();
    /**
     * Список email зарегистрированных пользователей.
     */
    private final Set<String> registeredEmail = new HashSet<>();
    /**
     * Подключение службы регистрации пользователя в фильмотеке
     */
    private final RegistrationService<User> registrationService;

    /**
     * Метод создает в списке пользователей хранилища нового пользователя с уникальным ID и email.
     *
     * @param user регистрируемый пользователь
     * @return этот же пользователь с зарегистрированным ID
     */
    @Override
    public User createUser(User user) {
        log.info("Создание записи о пользователе в хранилище:");
        String email = user.getEmail();
        if (registeredEmail.contains(email)) {
            String message =
                    String.format("Создать запись о пользователе не удалось, email %s уже зарегистрирован!", email);
            log.warn(message);
            throw new EntityAlreadyExistsException(this.getClass().getName(), USER_STORAGE_INTERNAL_ERROR, message);
        } else {
            users.put(registrationService.register(user), user);
            registeredEmail.add(email);
            log.info("Пользователь добавлен в хранилище: {}", user);
            return user;
        }
    }

    /**
     * Метод обновляет в списке пользователей хранилища существующего пользователя.
     *
     * @param user пользователь, которого нужно найти и обновить
     * @return обновленный пользователь
     */
    @Override
    public User updateUser(User user) {
        log.info("Обновление записи о пользователе в хранилище:");
        int id = user.getId();
        if (users.containsKey(id)) {
            String newEmail = user.getEmail();
            if (!registeredEmail.add(newEmail)) {
                String message = String.format(
                        "Обновить запись о пользователе не удалось, пользователь %s уже зарегистрирован!", newEmail);
                log.warn(message);
                throw new EntityAlreadyExistsException(this.getClass().getName(), USER_STORAGE_INTERNAL_ERROR, message);
            }
            registeredEmail.remove(users.get(id).getEmail());
            users.put(id, user);
            log.info("Пользователь успешно обновлен в хранилище: {}", user);
            return user;
        } else {
            String message =
                    String.format("Обновить запись о пользователе не удалось, пользователь с ID %d не найден!", id);
            log.warn(message);
            throw new EntityNotFoundException(this.getClass().getName(), USER_STORAGE_INTERNAL_ERROR, message);
        }
    }

    /**
     * Метод удаляет пользователя из хранилища.
     *
     * @param userId удаляемый пользователь
     */
    @Override
    public User deleteUser(int userId) {
        log.info("Удаление записи о пользователе из хранилища:");
        User user = users.remove(userId);
        if (user == null) {
            String message =
                    String.format("Удалить запись о пользователе не удалось, пользователь с ID %d не найден!", userId);
            log.warn(message);
            throw new EntityNotFoundException(this.getClass().getName(), USER_STORAGE_INTERNAL_ERROR, message);
        } else {
            registeredEmail.remove(user.getEmail());
            log.warn("Пользователь ID {} удален из хранилища", userId);
            return user;
        }
    }

    /**
     * Метод возвращает список всех пользователей из хранилища.
     *
     * @return список пользователей, может быть пустым
     */
    @Override
    public List<User> getAllUsers() {
        log.info("Возвращен список всех пользователей из хранилища");
        return List.copyOf(users.values());
    }

    /**
     * Метод возвращает пользователя по его ID
     *
     * @param userId ID пользователя
     * @return искомый пользователь
     */
    @Override
    public User getUser(int userId) {
        log.info("Получение записи о пользователе из хранилища");
        User user = users.get(userId);
        if (user == null) {
            String message =
                    String.format("Получить запись о пользователе не удалось, пользователь с ID %d не найден!", userId);
            log.warn(message);
            throw new EntityNotFoundException(this.getClass().getName(), USER_STORAGE_INTERNAL_ERROR, message);
        }
        log.info("Получен пользователь ID {}", userId);
        return user;
    }

}
