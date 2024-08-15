package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.entity.Event;
import ru.yandex.practicum.filmorate.repository.EventRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService implements BaseEventService {
    private final EventRepository eventRepository;
    private final UserService userService;

    @Override
    public Collection<Event> getFeed(int userId) {
        userService.getUser(userId);
        return eventRepository.getAllFriendsEventsByUserId(userId);
    }
}
