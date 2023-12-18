package ru.yandex.practicum.filmorate.storages.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.interfaces.RegistrationService;
import ru.yandex.practicum.filmorate.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.models.User;

import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * Хранилище и бизнес-логика работы со списком клиентов фильмотеки в памяти.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private static final String STORAGE_INTERNAL_ERROR = "Выполнение операций при работе с хранилищем в памяти";
    /**
     * Список-хранилище пользователей фильмотеки в памяти.
     */
    private final Map<Integer, User> users = new HashMap<>();
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
        String email = user.getEmail();
        if (registeredEmail.contains(email)) {
            String message =
                    String.format("Создать запись о пользователе не удалось, %s уже зарегистрирован!", email);
            log.warn(message);
            throw new UserAlreadyExistsException(STORAGE_INTERNAL_ERROR, message);
        } else {
            users.put(registrationService.register(user), user);
            registeredEmail.add(email);
            log.info("Пользователь успешно добавлен в хранилище: {}, {}, {} ", user.getId(), user.getLogin(), email);
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
        int id = user.getId();
        if (users.containsKey(id)) {
            String newEmail = user.getEmail();
            if (!registeredEmail.add(newEmail)) {
                String message = String.format(
                        "Обновить запись о пользователе не удалось, пользователь %s уже зарегистрирован!", newEmail);
                log.warn(message);
                throw new UserAlreadyExistsException(STORAGE_INTERNAL_ERROR, message);
            }
            registeredEmail.remove(users.get(id).getEmail());
            users.put(id, user);
            log.info("Пользователь успешно добавлен в хранилище: {}, {}, {}", id, user.getName(), newEmail);
            return user;
        } else {
            String message =
                    String.format("Обновить запись о пользователе не удалось, пользователь с ID %d не найден!", id);
            log.warn(message);
            throw new UserNotFoundException(STORAGE_INTERNAL_ERROR, message);
        }
    }

    /**
     * Метод удаляет пользователя из хранилища.
     *
     * @param userId удаляемый пользователь
     */
    @Override
    public User deleteUser(int userId) {
        User user = users.remove(userId);
        if (user == null) {
            String message =
                    String.format("Удалить запись о пользователе не удалось, пользователь с ID %d не найден!", userId);
            log.warn(message);
            throw new UserNotFoundException(STORAGE_INTERNAL_ERROR, message);
        } else {
            registeredEmail.remove(user.getEmail());
            log.warn("Пользователь ID {} успешно удален", userId);
            return user;
        }
    }

    /**
     * Метод возвращает список всех пользователей из хранилища.
     *
     * @return список пользователей, может быть пустым
     */
    @Override
    public @NotNull List<User> getAllUsers() {
        log.info("Получен список всех пользователей хранилища");
        return List.copyOf(users.values());
    }

    /**
     * Метод возвращает пользователя по его ID
     * @param userId ID пользователя
     * @return искомый пользователь
     */
    @Override
    public User getUser(int userId) {
        User user = users.get(userId);
        if (user == null) {
            String message =
                    String.format("Получить запись о пользователе не удалось, пользователь с ID %d не найден!", userId);
            log.warn(message);
            throw new UserNotFoundException(STORAGE_INTERNAL_ERROR, message);
        }
        log.info("Получен пользователь ID {}", userId);
        return user;
    }

}
