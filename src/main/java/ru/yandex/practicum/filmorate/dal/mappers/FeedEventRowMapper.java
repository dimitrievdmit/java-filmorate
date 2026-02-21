package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.enums.EventOperation;
import ru.yandex.practicum.filmorate.enums.EventType;
import ru.yandex.practicum.filmorate.model.FeedEvent;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FeedEventRowMapper implements RowMapper<FeedEvent> {
    @Override
    public FeedEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
        FeedEvent e = new FeedEvent();
        e.setId(rs.getLong("id"));
        e.setUserId(rs.getLong("user_id"));
        e.setEventType(EventType.valueOf(rs.getString("event_type")));
        e.setOperation(EventOperation.valueOf(rs.getString("operation")));
        e.setEntityId(rs.getLong("entity_id"));
        e.setTimestamp(rs.getLong("timestamp"));
        return e;
    }
}
