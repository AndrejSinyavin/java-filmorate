package ru.yandex.practicum.filmorate.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class Event {
    @NotNull(message = "ID события не может быть null")
    @Positive(message = "ID события не может быть отрицательным значением")
    private int eventId;
    @NotNull(message = "Дата события не может быть null")
    private long timestamp;
    @NotNull(message = "ID пользователя не может быть null")
    @Positive(message = "ID пользователя не может быть отрицательным значением")
    private int userId;
    @NotBlank(message = "Тип события не может быть null")
    private String eventType;
    @NotBlank(message = "Операция события не может быть null")
    private String operation;
    @NotNull(message = "ID сущности не может быть null")
    @Positive(message = "ID сущности не может быть отрицательным значением")
    private int entityId;

    public Event(long instant, int userId, String eventType, String operation, int entityId) {
        this.timestamp = instant;
        this.userId = userId;
        this.eventType = eventType;
        this.operation = operation;
        this.entityId = entityId;
    }
}