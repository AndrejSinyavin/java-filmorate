package ru.yandex.practicum.filmorate.storages;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.services.RegistrationService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.*;

/**
 * Хранилище и бизнес-логика работы со списком клиентов фильмотеки в памяти.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Valid
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
     * @return эта же запись с зарегистрированным новым ID; либо пустое значение -
     * если пользователь с такой почтой уже зарегистрирован
     */
    @Override
    public Optional<User> createUser(@NotNull(message = ENTITY_ERROR) User user) {
        log.info("Создание записи о пользователе в хранилище:");
        String email = user.getEmail();
        if (registeredEmail.contains(email)) {
            log.error("Создать запись о пользователе не удалось, почта {} уже зарегистрирована!", email);
            return Optional.empty();
        } else {
            users.put(registrationService.register(user), user);
            registeredEmail.add(email);
            log.info("Запись о пользователе добавлена в хранилище: {}", user);
            return Optional.of(user);
        }
    }

    /**
     * Метод обновляет в списке пользователей хранилища существующего пользователя.
     *
     * @param user запись о пользователе, которую нужно найти и обновить
     * @return обновленная запись о пользователе, если она уже существовала; иначе пустое значение
     */
    @Override
    public Optional<User> updateUser(@NotNull(message = ENTITY_ERROR) User user) {
        log.info("Обновление записи о пользователе в хранилище:");
        int id = user.getId();
        String newEmail = user.getEmail();
        User target = users.get(id);
        if (target == null) {
            log.error("Запись о пользователе не найдена в хранилище: {}", user);
            return Optional.empty();
        }
        if (!newEmail.equals(target.getEmail()) && registeredEmail.contains(newEmail)) {
            log.error("Обновить запись о пользователе не удалось, почта {} уже зарегистрирована!", newEmail);
            return Optional.empty();
        } else {
            registeredEmail.add(newEmail);
        }
        users.put(id, user);
        log.info("Запись о пользователе обновлена в хранилище: {}", user);
        return Optional.of(user);
    }

    /**
     * Метод удаляет запись о пользователе из хранилища.
     *
     * @param userId ID удаляемого пользователя
     * @return запись о пользователе, который был удален; пустое значение - если запись не была найдена
     */
    @Override
    public Optional<User> deleteUser(@Positive(message = ID_ERROR) int userId) {
        log.info("Удаление записи о пользователе из хранилища:");
        User deletedUser = users.remove(userId);
        if (deletedUser == null) {
            log.error("Удалить запись о пользователе не удалось, пользователь с ID {} не найден!", userId);
            return Optional.empty();
        } else {
            if (!registeredEmail.remove(deletedUser.getEmail())) {
                log.warn("Не найдена запись о почте удаляемого пользователя, " +
                        "возможно нарушение структур данных хранилища!");
            }
            log.info("Запись о пользователе {} удалена из хранилища", deletedUser);
            return Optional.of(deletedUser);
        }
    }

    /**
     * Метод возвращает список всех пользователей сервиса из хранилища.
     *
     * @return список пользователей, может быть пустым
     */
    @Override
    public List<User> getAllUsers() {
        log.info("Возвращен список всех пользователей из хранилища");
        return List.copyOf(users.values());
    }

    /**
     * Метод возвращает запись о пользователе по его ID
     *
     * @param userId ID искомого пользователя
     * @return запись о пользователе; либо пустое значение, если не найден
     */
    @Override
    public Optional<User> getUser(@Positive(message = ID_ERROR) int userId) {
        log.info("Получение записи о пользователе ID {} из хранилища", userId);
        return Optional.ofNullable(users.get(userId));
    }

}
