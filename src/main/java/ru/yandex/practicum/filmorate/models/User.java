package ru.yandex.practicum.filmorate.models;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * Класс описывает пользователя фильмотеки
 */
@Data
@Validated
public final class User {
    @DecimalMin(value = "0", message = "ID записи не может быть отрицательным значением")
    private int id;

    private String name;

    @NotBlank(message = "Логин не может быть пустым")
    private String login;

    @Email(message = "Неверный email")
    @NotBlank(message = "email не может быть пустым")
    private String email;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String birthday;
}