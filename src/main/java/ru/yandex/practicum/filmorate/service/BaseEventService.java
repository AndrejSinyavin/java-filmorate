package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.entity.Event;

import java.util.Collection;

public interface BaseEventService {
    Collection<Event> getFeed(int userId);
}
