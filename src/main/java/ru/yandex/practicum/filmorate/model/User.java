package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class User {
    @NotBlank(message = "Логин не может быть пустым!")
    private String login;
    @NotNull(message = "Имя пользователя не может NULL")
    private String name;
    @Email(message = "Неверный формат даты!")
    private String email;
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private String birthday;
}
