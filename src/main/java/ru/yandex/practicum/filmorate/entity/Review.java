package ru.yandex.practicum.filmorate.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review {
    Integer reviewId;
    @NotBlank
    String content;
    @JsonProperty("isPositive")
    @NotNull
    Boolean isPositive;
    @NotNull
    Integer userId;
    @NotNull
    Integer filmId;
    Integer useful = 0;
}
