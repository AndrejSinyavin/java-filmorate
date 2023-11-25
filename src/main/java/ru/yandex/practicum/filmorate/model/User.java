package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public final class User {
    private int id;
    private String name;
    @NotBlank(message = "Логин не может быть пустым!")
    private String login;
    @Email(message = "Неверный формат даты!")
    private String email;
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private String birthday;
}
