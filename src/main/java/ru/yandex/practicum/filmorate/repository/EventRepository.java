package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.entity.Event;

import java.util.Collection;

public interface EventRepository {
    void create(Event event);

    Collection<Event> getAllFriendsEventsByUserId(int userId);

    void update(Event event);

    void delete(Event event);
}
