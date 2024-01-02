package ru.yandex.practicum.filmorate.models;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDate;

/**
 * Класс описывает поля записи "пользователя" фильмотеки
 */
@Data
public class User {
    @DecimalMin(value = "0", message = "ID записи не может быть отрицательным значением")
    private int id;

    private String name;

    @NotBlank(message = "Логин не может быть пустым")
    private String login;

    @Email(message = "Неверный email")
    @NotBlank(message = "email не может быть пустым")
    private String email;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
}
