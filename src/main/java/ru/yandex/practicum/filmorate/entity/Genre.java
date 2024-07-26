package ru.yandex.practicum.filmorate.entity;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

/**
 * Жанр фильма
 */
@Data
@AllArgsConstructor
public class Genre {
    @NotNull(message = "ID MPA-рейтинга не может быть NULL")
    Set<String> genres;
}
