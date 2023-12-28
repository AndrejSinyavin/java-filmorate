package ru.yandex.practicum.filmorate.storages.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.interfaces.RegistrationService;
import ru.yandex.practicum.filmorate.interfaces.UserStorage;
import ru.yandex.practicum.filmorate.models.User;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.*;

/**
 * Хранилище и бизнес-логика работы со списком клиентов фильмотеки в памяти.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
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
    public Optional<User> createUser(@NotNull User user) {
        log.info("Создание записи о пользователе в хранилище:");
        String email = user.getEmail();
        if (registeredEmail.contains(email)) {
            log.warn("Создать запись о пользователе не удалось, имейл {} уже зарегистрирован!", email);
            return Optional.empty();
        } else {
            users.put(registrationService.register(user), user);
            registeredEmail.add(email);
            log.info("Пользователь добавлен в хранилище: {}", user);
            return Optional.of(user);
        }
    }

    /**
     * Метод обновляет в списке пользователей хранилища существующего пользователя.
     *
     * @param user пользователь, которого нужно найти и обновить
     * @return обновленный пользователь
     */
    @Override
    public Optional<User> updateUser(@NotNull User user) {
        log.info("Обновление записи о пользователе в хранилище:");
        int id = user.getId();
        if (!Objects.isNull(users.put(id, user))) {
            log.info("Запись о пользователе обновлена в хранилище: {}", user);
            return Optional.of(user);
        } else {
            log.warn("Обновить запись о пользователе не удалось, пользователь с ID {} не найден!", id);
            return Optional.empty();
        }
    }

    /**
     * Метод удаляет запись о пользователе из хранилища.
     *
     * @param userId удаляемый пользователь
     * @return true, если запись удалена, false - если такой записи не было
     */
    @Override
    public boolean deleteUser(@Positive int userId) {
        log.info("Удаление записи о пользователе из хранилища:");
        if (Objects.isNull(users.remove(userId))) {
            log.warn("Удалить запись о пользователе не удалось, пользователь с ID {} не найден!", userId);
            return false;
        } else {
            registeredEmail.remove(users.get(userId).getEmail());
            log.warn("Запись о пользователе ID {} удалена из хранилища", userId);
            return true;
        }
    }

    /**
     * Метод возвращает список всех пользователей из хранилища.
     *
     * @return список пользователей, может быть пустым
     */
    @Override
    public Optional<List<User>> getAllUsers() {
        log.info("Возвращен список всех пользователей из хранилища");
        return Optional.of(List.copyOf(users.values()));
    }

    /**
     * Метод возвращает запись о пользователе по его ID
     *
     * @param userId ID пользователя
     * @return запись о пользователе
     */
    @Override
    public Optional<User> getUser(@Positive int userId) {
        log.info("Получение записи о пользователе ID {} из хранилища", userId);
        return Optional.ofNullable(users.get(userId));
    }

}
