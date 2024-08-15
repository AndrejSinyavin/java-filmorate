package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.entity.Event;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.repository.EventRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService implements BaseEventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public Collection<Event> getFeed(int userId) {
        if (userRepository.getUser(userId).isEmpty()) {
            throw new EntityNotFoundException(getClass().getSimpleName(),
                    EntityNotFoundException.class.getName(),
                    "Пользователь с указанным id не найден");
        }
        return eventRepository.getAllFriendsEventsByUserId(userId);
    }
}
