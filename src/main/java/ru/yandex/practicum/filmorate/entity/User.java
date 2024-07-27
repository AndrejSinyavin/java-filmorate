package ru.yandex.practicum.filmorate.entity;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * Класс описывает поля записи "пользователя" фильмотеки
 */
@Data
public class User {
    @DecimalMin(value = "0", message = "ID записи не может быть отрицательным значением")
    private int id;

    @NotBlank(message = "Логин не может быть пустым")
    private String login;
    private String name;

    @Email(message = "Неверный email")
    @NotBlank(message = "email не может быть пустым")
    private String email;

    @NotNull
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
}
