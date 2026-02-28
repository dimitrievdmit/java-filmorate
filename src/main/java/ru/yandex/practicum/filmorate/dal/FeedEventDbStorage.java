package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FeedEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@Repository
public class FeedEventDbStorage implements FeedEventStorage {

    private final NamedParameterJdbcTemplate jdbc;
    private final RowMapper<FeedEvent> rowMapper;

    private static final String INSERT_EVENT = """
            INSERT INTO user_feed (user_id, event_type, operation, entity_id, timestamp)
            VALUES (:userId, :eventType, :operation, :entityId, :timestamp)
            """;

    private static final String SELECT_FEED = """
            SELECT id, user_id, event_type, operation, entity_id, timestamp
            FROM user_feed
            WHERE user_id = :userId
            ORDER BY timestamp
            LIMIT :limit
            """;

    public FeedEventDbStorage(NamedParameterJdbcTemplate jdbc, RowMapper<FeedEvent> rowMapper) {
        this.jdbc = jdbc;
        this.rowMapper = rowMapper;
    }

    @Override
    public void addEvent(FeedEvent event) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", event.getUserId());
        params.put("eventType", event.getEventType() != null ? event.getEventType().name() : null);
        params.put("operation", event.getOperation() != null ? event.getOperation().name() : null);
        params.put("entityId", event.getEntityId());
        params.put("timestamp", event.getTimestamp());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource paramSource = new MapSqlParameterSource(params);
        jdbc.update(INSERT_EVENT, paramSource, keyHolder, new String[]{"id"});

        Number key = keyHolder.getKey();
        if (key != null) {
            event.setId(key.longValue());
        }
    }

    @Override
    public List<FeedEvent> getFeedForUser(Long userId, int limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("limit", limit);
        return jdbc.query(SELECT_FEED, params, rowMapper);
    }

}
