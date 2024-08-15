package ru.yandex.practicum.filmorate.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    @NotNull(message = "ID события не может быть null")
    @Positive(message = "ID события не может быть отрицательным значением")
    private int eventId;
    @NotNull(message = "Дата события не может быть null")
    long timestamp;
    @NotNull(message = "ID пользователя не может быть null")
    @Positive(message = "ID пользователя не может быть отрицательным значением")
    int userId;
    @NotBlank(message = "Тип события не может быть пустым")
    String eventType;
    @NotBlank(message = "Операция события не может быть пустым")
    String operation;
    @NotNull(message = "ID сущности не может быть null")
    @Positive(message = "ID сущности не может быть отрицательным значением")
    int entityId;

    public Event(long instant, int userId, String eventType, String operation, int entityId) {
        this.timestamp = instant;
        this.userId = userId;
        this.eventType = eventType;
        this.operation = operation;
        this.entityId = entityId;
    }
}