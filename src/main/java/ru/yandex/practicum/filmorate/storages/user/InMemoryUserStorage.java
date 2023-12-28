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
    private static final String ID_ERROR = "ID может быть только положительным значением";
    private static final String ENTITY_ERROR = "Пользователь не существует";
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
     * @param user запись о пользователе, которую нужно создать в хранилище
     * @return этот же пользователь с зарегистрированным ID; пустое значение - если пользователь с такой почтой
     * уже зарегистрирован
     */
    @Override
    public Optional<User> createUser(@NotNull(message = ENTITY_ERROR) User user) {
        log.info("Создание записи о пользователе в хранилище:");
        String email = user.getEmail();
        if (registeredEmail.contains(email)) {
            log.warn("Создать запись о пользователе не удалось, почта {} уже зарегистрирована!", email);
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
     * @return обновленный пользователь, если он уже существовал, иначе пустое значение
     */
    @Override
    public Optional<User> updateUser(@NotNull(message = ENTITY_ERROR) User user) {
        log.info("Обновление записи о пользователе в хранилище:");
        int id = user.getId();
        if (users.containsKey(id)) {
            users.put(id, user);
            log.info("Запись о пользователе обновлена в хранилище: {}", user);
            return Optional.of(user);
        } else {
            log.warn("Запись о пользователе не найдена в хранилище: {}", user);
            return Optional.empty();
        }
    }

    /**
     * Метод удаляет запись о пользователе из хранилища.
     *
     * @param userId удаляемый пользователь
     * @return удаленный пользователь, если запись удалена, пустое значение - если такой записи не было
     */
    @Override
    public Optional<User> deleteUser(@Positive(message = ID_ERROR) int userId) {
        log.info("Удаление записи о пользователе из хранилища:");
        User deletedUser = users.remove(userId);
        if (Objects.isNull(deletedUser)) {
            log.warn("Удалить запись о пользователе не удалось, пользователь с ID {} не найден!", userId);
            return Optional.empty();
        } else {
            registeredEmail.remove(deletedUser.getEmail());
            log.warn("Запись о пользователе {} удалена из хранилища", deletedUser);
            return Optional.of(deletedUser);
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
    public Optional<User> getUser(@Positive(message = ID_ERROR) int userId) {
        log.info("Получение записи о пользователе ID {} из хранилища", userId);
        return Optional.ofNullable(users.get(userId));
    }

}
