package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import ru.yandex.practicum.filmorate.entity.*;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.FriendRepository;
import ru.yandex.practicum.filmorate.repository.RatingRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;
import ru.yandex.practicum.filmorate.repository.UtilRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@ComponentScan
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DisplayName("Тесты работы с БД")
class FilmoRateApplicationTests {
    private final FilmRepository films;
    private final FriendRepository friends;
    private final UserRepository users;
    private final RatingRepository likes;
    private final UtilRepository utils;
    private Optional<Film> film;
    private Optional<User> user;
    private Genre genre;
    private Mpa mpa;

    @Test
    @DisplayName("Фильм1 читается из БД")
    public void testGetFilm() {

        film = films.getFilm(1);

        assertThat(film)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1));
    }

    @Test
    @DisplayName("Фильм6 не существует в БД")
    public void testGetUnknownFilm() {

        film = films.getFilm(6);

        assertThat(film).isNotPresent();
    }

    @Test
    @DisplayName("В БД есть 5 фильмов")
    public void testGetFilms() {

        var listFilms = films.getFilms();

        assertThat(listFilms).hasSize(5);
    }

    @Test
    @DisplayName("В БД корректно создан и затем прочитан новый фильм")
    public void testCreateFilm() {

        genre = utils.getGenreById(1);
        mpa = utils.getMpaById(1);
        Film testFilm = new Film(
                0,
                "name",
                "description",
                LocalDate.of(2024, 7, 29),
                120,
                1000,
                mpa,
                List.of(genre),
                new TreeSet<>(Director::compareTo));
        films.createFilm(testFilm);
        film = films.getFilm(6);

        assertThat(film)
                .isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film).hasFieldOrPropertyWithValue("id", 6);
                    assertThat(film).hasFieldOrPropertyWithValue("name", testFilm.getName());
                    assertThat(film).hasFieldOrPropertyWithValue("description", testFilm.getDescription());
                    assertThat(film).hasFieldOrPropertyWithValue("releaseDate", testFilm.getReleaseDate());
                    assertThat(film).hasFieldOrPropertyWithValue("duration", testFilm.getDuration());
                    assertThat(film).hasFieldOrPropertyWithValue("rate", 0);
                    assertThat(film).hasFieldOrPropertyWithValue("mpa", testFilm.getMpa());
                });
    }

    @Test
    @DisplayName("Обновляем фильм c ID 5")
    public void testUpdateFilm() {

        film = films.getFilm(5);
        var testFilm = film.get();
        testFilm.setDescription("NEWdescription");
        films.updateFilm(testFilm);
        film = films.getFilm(5);

        assertThat(film)
                .isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film).hasFieldOrPropertyWithValue("id", 5);
                    assertThat(film).hasFieldOrPropertyWithValue("description", testFilm.getDescription());
                });
    }

    @Test
    @DisplayName("Проверяем топ-2 рейтинга фильмов")
    public void testGetPopularFilm() {
        likes.likeFilm(5, 1);
        likes.likeFilm(5, 2);
        likes.likeFilm(4, 3);
        var top = films.getPopularFilm(2);

        assertThat(top).hasSize(2);
        var testFilm = top.getFirst();

        assertThat(testFilm).hasFieldOrPropertyWithValue("id", 5);
        assertThat(testFilm).hasFieldOrPropertyWithValue("rate", 2);
        testFilm = top.getLast();
        assertThat(testFilm).hasFieldOrPropertyWithValue("id", 4);
        assertThat(testFilm).hasFieldOrPropertyWithValue("rate", 1);
    }

    @Test
    @DisplayName("Создание пользователя")
    public void testCreateUser() {
        User testUser = new User(
                0,
                "login",
                "name",
                "email@yandex.ru",
                LocalDate.of(2000, 1, 1)
        );
        users.createUser(testUser);
        user = users.getUser(6);

        assertThat(user)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user).hasFieldOrPropertyWithValue("id", 6);
                    assertThat(user).hasFieldOrPropertyWithValue("login", testUser.getLogin());
                    assertThat(user).hasFieldOrPropertyWithValue("name", testUser.getName());
                });
    }

    @Test
    @DisplayName("Обновляем пользователя c ID 1")
    public void testUpdateUser() {

        user = users.getUser(1);
        var testUser = user.get();
        testUser.setName("NEWname");
        users.updateUser(testUser);
        user = users.getUser(1);

        assertThat(user)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user).hasFieldOrPropertyWithValue("id", 1);
                    assertThat(user).hasFieldOrPropertyWithValue("name", testUser.getName());
                });
    }

    @Test
    @DisplayName("Получаем пользователя c ID 1")
    public void testGetUser() {

        user = users.getUser(1);

        assertThat(user)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1));
    }

    @Test
    @DisplayName("В БД есть 5 пользователей")
    public void testGetUsers() {

        var listUsers = films.getFilms();

        assertThat(listUsers).hasSize(5);
    }

    @Test
    @DisplayName("Добавляем юзера 2 в друзья юзеру 1, и удаляем")
    public void testAddFriend() {
        var friendListUser1 = friends.getFriends(1);
        assertThat(friendListUser1).hasSize(0);
        friends.addFriend(1, 2);
        friendListUser1 = friends.getFriends(1);
        assertThat(friendListUser1).hasSize(1);
        var friend = friendListUser1.getFirst();
        assertThat(friend)
                .hasFieldOrPropertyWithValue("id", 2)
                .hasFieldOrPropertyWithValue("name", "name2");
        friends.deleteFriend(1, 2);
        friendListUser1 = friends.getFriends(1);
        assertThat(friendListUser1).hasSize(0);
    }

    @Test
    @DisplayName("Получение списка друзей пользователя")
    public void testGetFriends() {
        var friendListUser1 = friends.getFriends(1);
        assertThat(friendListUser1).hasSize(0);
        friends.addFriend(1, 2);
        friends.addFriend(1, 3);
        friends.addFriend(1, 4);
        friends.addFriend(1, 5);
        friendListUser1 = friends.getFriends(1);
        assertThat(friendListUser1).hasSize(4);
    }

    @Test
    @DisplayName("Получение списка общих друзей двух пользователей")
    public void testGetCommonFriends() {
        var commonFriendsList = friends.getCommonFriends(1, 2);
        assertThat(commonFriendsList).hasSize(0);
        var friendListUser1 = friends.getFriends(1);
        assertThat(friendListUser1).hasSize(0);
        var friendListUser2 = friends.getFriends(2);
        assertThat(friendListUser2).hasSize(0);
        friends.addFriend(1, 2);
        friends.addFriend(1, 3);
        friends.addFriend(2, 1);
        friends.addFriend(2, 3);
        friends.addFriend(2, 4);
        friends.addFriend(3, 4);
        friends.addFriend(4, 5);
        var commonFriendUser1 = friends.getCommonFriends(1, 2);
        assertThat(commonFriendUser1).hasSize(1);
        assertThat(commonFriendUser1.getFirst()).hasFieldOrPropertyWithValue("id", 3);
        var commonFriendUser2 = friends.getCommonFriends(2, 3);
        assertThat(commonFriendUser2).hasSize(1);
        assertThat(commonFriendUser2.getFirst()).hasFieldOrPropertyWithValue("id", 4);
        var commonFriendUser3 = friends.getCommonFriends(3, 4);
        assertThat(commonFriendUser3).hasSize(0);
        var commonFriendUser4 = friends.getCommonFriends(4, 5);
        assertThat(commonFriendUser4).hasSize(0);
    }

    @Test
    @DisplayName("Удаляем друзей юзеров")
    public void testDeleteFriend() {
        var user1FriendList = friends.getFriends(1);
        var user2FriendList = friends.getFriends(2);
        assertThat(user1FriendList).hasSize(0);
        assertThat(user2FriendList).hasSize(0);
        friends.addFriend(1, 2);
        user1FriendList = friends.getFriends(1);
        assertThat(user1FriendList).hasSize(1);
        assertThat(user2FriendList).hasSize(0);
        friends.deleteFriend(1, 2);
        user1FriendList = friends.getFriends(1);
        assertThat(user1FriendList).hasSize(0);
        user2FriendList = friends.getFriends(2);
        assertThat(user2FriendList).hasSize(0);
    }

    @Test
    @DisplayName("Пользователи ставят лайк фильму")
    public void testLikeFilm() {
        var rate = likes.getFilmRate(1);
        assertThat(rate).isEqualTo(0);
        likes.likeFilm(1, 1);
        rate = likes.getFilmRate(1);
        assertThat(rate).isEqualTo(1);
        likes.dislikeFilm(1, 1);
        rate = likes.getFilmRate(1);
        assertThat(rate).isEqualTo(0);
        likes.likeFilm(1, 1);
        likes.likeFilm(1, 2);
        rate = likes.getFilmRate(1);
        assertThat(rate).isEqualTo(2);
    }

    @Test
    @DisplayName("Удаление лайка фильму")
    public void testUnLikeFilm() {
        var rate = likes.getFilmRate(1);
        assertThat(rate).isEqualTo(0);
        likes.likeFilm(1, 1);
        rate = likes.getFilmRate(1);
        assertThat(rate).isEqualTo(1);
        likes.dislikeFilm(1, 1);
        rate = likes.getFilmRate(1);
        assertThat(rate).isEqualTo(0);
    }

    @Test
    @DisplayName("Рейтинги фильмов")
    public void testGetFilmRate() {
        likes.likeFilm(1, 1);
        likes.likeFilm(1, 2);
        likes.likeFilm(1, 3);
        likes.likeFilm(2, 4);
        likes.likeFilm(2, 5);
        likes.likeFilm(3, 1);
        var film1Rate = likes.getFilmRate(1);
        var film2Rate = likes.getFilmRate(2);
        var film3Rate = likes.getFilmRate(3);
        var film4Rate = likes.getFilmRate(4);
        assertThat(film1Rate).isEqualTo(3);
        assertThat(film2Rate).isEqualTo(2);
        assertThat(film3Rate).isEqualTo(1);
        assertThat(film4Rate).isEqualTo(0);
        var top = films.getPopularFilm(4);
        assertThat(top).hasSize(4);
        assertThat(top.get(0)).hasFieldOrPropertyWithValue("id", 1);
        assertThat(top.get(1)).hasFieldOrPropertyWithValue("id", 2);
        assertThat(top.get(2)).hasFieldOrPropertyWithValue("id", 3);
    }

    @Test
    @DisplayName("Получение жанра по его идентификатору")
    public void testGetGenreById() {
        assertThat(utils.getAllGenres()).hasSize(6);
        assertThat(utils.getGenreById(1)).hasFieldOrPropertyWithValue("id", 1);
        assertThat(utils.getGenreById(2)).hasFieldOrPropertyWithValue("id", 2);
        assertThat(utils.getGenreById(3)).hasFieldOrPropertyWithValue("id", 3);
        assertThat(utils.getGenreById(4)).hasFieldOrPropertyWithValue("id", 4);
        assertThat(utils.getGenreById(5)).hasFieldOrPropertyWithValue("id", 5);
        assertThat(utils.getGenreById(6)).hasFieldOrPropertyWithValue("id", 6);
        assertThat(utils.getGenreById(1)).hasFieldOrPropertyWithValue("name", "Комедия");
        assertThat(utils.getGenreById(2)).hasFieldOrPropertyWithValue("name", "Драма");
        assertThat(utils.getGenreById(3)).hasFieldOrPropertyWithValue("name", "Мультфильм");
        assertThat(utils.getGenreById(4)).hasFieldOrPropertyWithValue("name", "Триллер");
        assertThat(utils.getGenreById(5)).hasFieldOrPropertyWithValue("name", "Документальный");
        assertThat(utils.getGenreById(6)).hasFieldOrPropertyWithValue("name", "Боевик");
    }

    @Test
    @DisplayName("Получение всех жанров")
    public void testGetAllGenres() {
        assertThat(utils.getAllGenres()).hasSize(6);
    }

    @Test
    @DisplayName("Получение MPA по его идентификатору")
    public void testGetMpaById() {
        assertThat(utils.getAllMpa()).hasSize(5);
        assertThat(utils.getMpaById(1)).hasFieldOrPropertyWithValue("id", 1);
        assertThat(utils.getMpaById(2)).hasFieldOrPropertyWithValue("id", 2);
        assertThat(utils.getMpaById(3)).hasFieldOrPropertyWithValue("id", 3);
        assertThat(utils.getMpaById(4)).hasFieldOrPropertyWithValue("id", 4);
        assertThat(utils.getMpaById(5)).hasFieldOrPropertyWithValue("id", 5);
        assertThat(utils.getMpaById(1)).hasFieldOrPropertyWithValue("name", "G");
        assertThat(utils.getMpaById(2)).hasFieldOrPropertyWithValue("name", "PG");
        assertThat(utils.getMpaById(3)).hasFieldOrPropertyWithValue("name", "PG-13");
        assertThat(utils.getMpaById(4)).hasFieldOrPropertyWithValue("name", "R");
        assertThat(utils.getMpaById(5)).hasFieldOrPropertyWithValue("name", "NC-17");
    }

    @Test
    @DisplayName("Получение всех MPA")
    public void testGetAllMpa() {
        assertThat(utils.getAllMpa()).hasSize(5);
    }

    @Test
    @DisplayName("Получение фильмов по списку ID")
    public void shouldGetFilmsListByFilmsIdList() {
        List<Film> filmsList = films.getFilmsByIds(List.of(1, 2, 3));
        assertThat(filmsList)
                .hasSize(3);
    }

    @Test
    @DisplayName("Получение списка всех лайков")
    public void shouldGetAllLikes() {
        likes.likeFilm(5, 1);
        likes.likeFilm(5, 2);
        likes.likeFilm(4, 3);

        List<Like> testLikes = new ArrayList<>();
        testLikes.add(new Like(1, 5));
        testLikes.add(new Like(2, 5));
        testLikes.add(new Like(3, 4));

        assertThat(likes.getLikes()).isEqualTo(testLikes);
    }

    @Test
    @DisplayName("Проверка есть ли лайки у пользователя")
    public void shouldReturnTrueWhenUserHasLikes() {
        assertThat(likes.isUserHasLikes(1)).isEqualTo(false);
        likes.likeFilm(5, 1);
        assertThat(likes.isUserHasLikes(1)).isEqualTo(true);
    }

    @Test
    @DisplayName("Должен возвращать список общих фильмоа отсортированных по популярности")
    public void shouldReturnSortedCommonFilmsList() {
        likes.likeFilm(5, 1);
        likes.likeFilm(5, 2);
        likes.likeFilm(4, 1);
        likes.likeFilm(4, 2);
        likes.likeFilm(5, 3);
        List<Film> commonFilms = films.getCommonFilms(1, 2);

        assertThat(commonFilms)
                .hasSize(2);

        assertThat(commonFilms.get(0))
                .hasFieldOrPropertyWithValue("id", 5);

        assertThat(commonFilms.get(1))
                .hasFieldOrPropertyWithValue("id", 4);
    }

}