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
    public @NotNull User createUser(@NotNull User user) {
        String email = user.getEmail();
        if (registeredEmail.contains(email)) {
            String warning = "Пользователь с таким email уже зарегистрирован! ";
            log.warn(warning + email);
            throw new UserAlreadyExistsException(warning);
        } else {
            Integer id = registrationService.register(user);
            users.put(id, user);
            registeredEmail.add(email);
            log.info("Пользователь успешно добавлен в список пользователей фильмотеки: {}, {} ",
                    user.getName(), email);
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
    public @NotNull User updateUser(@NotNull User user) {
        Integer id = user.getId();
        if (users.containsKey(id)) {
            String newEmail = user.getEmail();
            if (registeredEmail.contains(newEmail)) {
                String warning = "Пользователь с таким email уже зарегистрирован! ";
                log.warn(warning + newEmail);
                throw new UserAlreadyExistsException(warning);
            }
            registeredEmail.remove(users.get(id).getEmail());
            registeredEmail.add(newEmail);
            users.put(id, user);
            log.info("Пользователь успешно обновлен в списке пользователей фильмотеки: {}, {}",
                    user.getName(), newEmail);
            return user;
        } else {
            String message = "Пользователь не найден!";
            log.warn(message);
            throw new UserNotFoundException(message);
        }
    }

    /**
     * Метод удаляет пользователя из хранилища.
     *
     * @param user удаляемый пользователь
     */
    @Override
    public User deleteUser(@NotNull User user) {
        user = users.remove(user.getId());
        if (user == null) {
            String message = "Пользователь не найден!";
            log.warn(message);
            throw new UserNotFoundException(message);
        } else {
            registeredEmail.remove(user.getEmail());
            log.warn("Пользователь удален");
            return user;
        }
    }

    /**
     * Метод возвращает список всех пользователей фильмотеки из хранилища.
     *
     * @return список пользователей, может быть пустым
     */
    @Override
    public @NotNull List<User> getAllUsers() {
        log.info("Возвращен список всех пользователей хранилища");
        return List.copyOf(users.values());
    }

    /**
     * Метод возвращает пользователя по его ID
     * @param userId ID пользователя
     * @return искомый пользователь
     */
    @Override
    public User getUser(Integer userId) {
        return users.get(userId);
    }

}
