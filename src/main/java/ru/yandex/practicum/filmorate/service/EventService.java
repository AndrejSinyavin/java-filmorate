package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.entity.Event;
import ru.yandex.practicum.filmorate.repository.EventRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class EventService implements BaseEventService {
    private final EventRepository eventRepository;

    @Override
    public Collection<Event> getFeed(int userId) {
        return eventRepository.getAllFriendsEventsByUserId(userId);
    }
}