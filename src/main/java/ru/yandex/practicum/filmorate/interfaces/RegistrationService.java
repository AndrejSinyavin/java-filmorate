package ru.yandex.practicum.filmorate.interfaces;

/**
 * Интерфейс для служб регистрации.
 *
 * @param <T> тип объекта, подлежащего регистрации.
 */
public interface RegistrationService<T> {
    /**
     * Метод регистрирует объект в службе регистрации соответствующего класса. Выдает объекту уникальный ID.
     *
     * @param obj регистрируемый объект
     * @return полученный ID
     */
    int register(T obj);
}
