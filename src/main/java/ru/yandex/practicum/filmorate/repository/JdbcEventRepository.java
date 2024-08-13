package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.entity.Event;

import java.sql.ResultSet;
import java.util.Collection;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JdbcEventRepository implements EventRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public void create(Event event) {
        log.info("Добавление события: {}", event);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update("INSERT INTO EVENTS (TIMESTAMP, USER_ID, EVENT_TYPE_NAME," +
                " OPERATION_NAME, ENTITY_ID) VALUES (:TIMESTAMP, :USER_ID," +
                " :EVENT_TYPE_NAME, :OPERATION_NAME, :ENTITY_ID);", toMap(event), keyHolder, new String[]{"EVENT_ID"});
        event.setId(keyHolder.getKeyAs(Integer.class));
        log.info("Событие {} добавлено в БД", event);
    }

    @Override
    public Collection<Event> getAllFriendsEventsByUserId(int userId) {
        log.info("Получение всех событий друзей пользователя с ID = {}", userId);
        var events = jdbc.query("SELECT * FROM EVENTS WHERE USER_ID = (" +
                "SELECT USER_ID FROM FRIENDSHIP_STATUSES WHERE FS_USER_ID = :USER_ID);", mapRow());
        log.info("Получены события друзей пользователя с ID = {}", userId);
        return events;
    }

    @Override
    public void update(Event event) {
        log.info("Обновление события: {}", event);
        jdbc.update("UPDATE EVENTS SET TIMESTAMP = :TIMESTAMP, USER_ID = :USER_ID," +
                " EVENT_TYPE_NAME = :EVENT_TYPE_NAME, OPERATION_NAME = :OPERATION_NAME," +
                " ENTITY_ID = :ENTITY_ID WHERE EVENT_ID = :EVENT_ID;", toMap(event));
        log.info("Событие {} обновлено", event);
    }

    @Override
    public void delete(Event event) {
        log.info("Удаление события: {}", event);
        jdbc.update("DELETE FROM EVENTS WHERE EVENT_ID = :EVENT_ID;", toMap(event));
        log.info("Событие {} удалено", event);
    }

    private RowMapper<Event> mapRow() {
        return (ResultSet rs, int rowNum) -> new Event(
                rs.getInt("EVENT_ID"),
                rs.getTimestamp("TIMESTAMP").toInstant(),
                rs.getInt("USER_ID"),
                rs.getString("EVENT_TYPE_NAME"),
                rs.getString("OPERATION_NAME"),
                rs.getInt("ENTITY_ID"));
    }

    private MapSqlParameterSource toMap(Event event) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("TIMESTAMP", event.getTimestamp());
        params.addValue("USER_ID", event.getUserId());
        params.addValue("EVENT_TYPE_NAME", event.getEventType());
        params.addValue("OPERATION_NAME", event.getOperation());
        params.addValue("ENTITY_ID", event.getId());
        return params;
    }
}
