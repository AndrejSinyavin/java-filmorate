//package ru.yandex.practicum.filmorate.controller;
//
//import jakarta.validation.constraints.Positive;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import ru.yandex.practicum.filmorate.entity.Mpa;
//import ru.yandex.practicum.filmorate.service.BaseMpaService;
//
//import java.util.List;
//
///**
// * Контроллер обработки REST-запросов для работы с MPA-рейтингами фильмотеки.
// */
//@Slf4j
//@Validated
//@RestController
//@RequestMapping("/mpa")
//@RequiredArgsConstructor
//public class MpaController {
//    private final String idError = "Ошибка! ID может быть только положительным значением";
//
//    /**
//     * Подключение сервиса работы с MPA-рейтингами.
//     */
////    private final BaseMpaService mpaService;
//
//    /**
//     * Endpoint обрабатывает запрос на получение MPA-рейтинга по его ID.
//     *
//     * @param id MPA-рейтинг
//     * @return {@link Mpa}
//     */
////    @GetMapping("/{id}")
////    public Mpa getMpaByID(@PathVariable @Positive(message = idError) final int id) {
////        log.info("Запрос ==> GET получить MPA-рейтинг по его ID {}", id);
//////        Mpa mpa = mpaService.getMpa(id);
////        log.info("Ответ <== 200 Ok. MPA-рейтинг отправлен ID {} {}", id, mpa);
////        return mpa;
////    }
//
//    /**
//     * Endpoint обрабатывает запрос на получение списка всех имеющихся MPA-рейтингов.
//     *
//     * @return список из {@link Mpa}
//     */
//    @GetMapping()
//    public List<Mpa> getAllMpa() {
////        log.info("Запрос ==> GET получить список всех MPA-рейтингов ");
////        List<Mpa> allMpa = mpaService.getAllMpa();
////        log.info("Ответ <== 200 Ok. Отправлен список MPA-рейтингов {}", allMpa);
////        return allMpa;
//    }
//}
