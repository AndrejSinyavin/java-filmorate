package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.entity.*;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.InternalServiceException;
import ru.yandex.practicum.filmorate.repository.*;


import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
     * Подключение репозитория для работы с фильмами.
     */
    private final FilmRepository films;
    /**
     * Подключение репозитория для работы с фильмами.
     */
    private final RatingRepository ratings;

    private final EventRepository events;
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
        events.create(new Event(Instant.now().toEpochMilli(), userId, EventType.FRIEND.toString(), EventOperation.ADD.toString(),
                friendId));
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
        events.create(new Event(Instant.now().toEpochMilli(), userId, EventType.FRIEND.toString(), EventOperation.REMOVE.toString(),
                friendId));
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

    /**
     * Метод возвращает список рекомендуемых фильмов пользователю по его ID
     *
     * @param userId ID пользователя
     * @return список рекомендуемых фильмов
     */
    @Override
    public List<Film> getRecommendations(int userId) {
        log.info("Получение фильмов рекомендуемых пользователю ID {}", userId);
        Map<Integer, List<Integer>> likesByUserId;
        List<Like> likesList = ratings.getAllLikes();
        if (!ratings.isUserHasLikes(userId)) {
            return new ArrayList<>();
        }
        if (likesList.isEmpty()) {
            throw new EntityNotFoundException(thisService, ratings.getClass().getName(),
                    "Таблица likes пуста");
        }
        likesByUserId = likesListToMap(likesList);
        Map<Integer, Double> filmsMatchPercents;
        filmsMatchPercents = getFilmsMatchPercents(userId, likesByUserId);
        Set<Integer> filmsIdsOfMostSimilarUsers = new HashSet<>();
        Optional<Double> optionalMaxMatchPercent = filmsMatchPercents.values().stream()
                .max(Comparator.comparing(Double::doubleValue));
        if (optionalMaxMatchPercent.isPresent()) {
            Double maxPercent = optionalMaxMatchPercent.get();
            if (maxPercent == 0.0)
                return new ArrayList<>();
            for (int otherUserId : filmsMatchPercents.keySet()) {
                if (filmsMatchPercents.get(otherUserId) >= maxPercent) {
                    for (int i = 0; i < likesByUserId.get(otherUserId).size(); i++) {
                        if (!likesByUserId.get(userId).contains(likesByUserId.get(otherUserId).get(i))) {
                            filmsIdsOfMostSimilarUsers.add(likesByUserId.get(otherUserId).get(i));
                        }
                    }
                }
            }
        }
        return films.getFilmsByIds(new ArrayList<>(filmsIdsOfMostSimilarUsers));
    }

    @Override
    public void deleteUserById(int userId) {
        users.removeUserById(userId);
    }

    /**
     * Метод преобразует список с объектами Like в HashMap вида:
     * key: id пользователя, value: список понравившихся фильмов
     *
     * @param likesList список объектов Like
     * @return HashMap с лайкнутыми фильмами, разделенных по ID пользовтелей
     */
    private Map<Integer, List<Integer>> likesListToMap(List<Like> likesList) {
        Map<Integer, List<Integer>> likesByUserId = new HashMap<>();
        for (Like like : likesList) {
            if (likesByUserId.get(like.getUserId()) == null) {
                List<Integer> films = new ArrayList<>();
                likesByUserId.put(like.getUserId(), films);
            }
            likesByUserId.get(like.getUserId()).add(like.getFilmId());
        }
        return likesByUserId;
    }

    /**
     * Метод возвращает HashMap с процентами совпадения
     * проставленных лайков пользователя с id = userId
     * с другими пользователями
     *
     * @param userId        id пользователя
     * @param likesByUserId HashMap с лайкнутыми фильмами, разделенных по ID пользователей
     * @return HashMap с долями совпадения
     */
    private Map<Integer, Double> getFilmsMatchPercents(Integer userId, Map<Integer, List<Integer>> likesByUserId) {
        Map<Integer, Double> filmsMatchPercent = new HashMap<>();
        for (int uid : likesByUserId.keySet()) {
            filmsMatchPercent.put(uid, 0.0);
        }
        for (int otherUserId : likesByUserId.keySet()) {
            if (otherUserId == userId)
                continue;
            for (int j = 0; j < likesByUserId.get(userId).size(); j++) {
                if (likesByUserId.get(otherUserId).contains(likesByUserId.get(userId).get(j))) {
                    filmsMatchPercent.put(otherUserId,
                            (filmsMatchPercent.get(otherUserId) + 1.0 / likesByUserId.get(otherUserId).size()));
                }
            }
        }
        return filmsMatchPercent;
    }
}


