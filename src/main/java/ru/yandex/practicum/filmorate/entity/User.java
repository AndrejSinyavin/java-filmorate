package ru.yandex.practicum.filmorate.entity;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Класс описывает поля записи "пользователя" фильмотеки
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @DecimalMin(value = "0", message = "ID записи не может быть отрицательным значением")
    int id;

    @NotBlank(message = "Логин не может быть пустым")
    String login;
    String name;

    @Email(message = "Неверный email")
    @NotBlank(message = "email не может быть пустым")
    String email;

    @NotNull
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate birthday;
}
