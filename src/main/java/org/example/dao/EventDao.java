package org.example.dao;

import org.example.dto.EventDto;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.example.jooq.generated.tables.Events.EVENTS;

public class EventDao
{
    private static final Logger log = LoggerFactory.getLogger(EventDao.class);
    private final DSLContext dsl;

    public EventDao(DSLContext dsl)
    {
        this.dsl = dsl;
    }

    public List<EventDto> findEvents()
    {
        log.info("Fetching all events");

        return dsl.selectFrom(EVENTS)
                .fetch()
                .map(this::toDto);
    }

    public Optional<EventDto> findEventById(int eventId)
    {
        log.info("Fetching event with id: {}", eventId);

        Record eventRecord = dsl.selectFrom(EVENTS)
                .where(EVENTS.EVENT_ID.eq(eventId))
                .fetchOne();

        return Optional.ofNullable(eventRecord)
                .map(this::toDto);
    }

    public List<EventDto> findEventsByName(String name)
    {
        log.info("Fetching events with name: {}", name);

        return dsl.selectFrom(EVENTS)
                .where(EVENTS.NAME.eq(name))
                .fetch()
                .map(this::toDto);
    }

    public List<EventDto> findEventsByDateRange(LocalDateTime startTime, LocalDateTime endTime)
    {
        log.info("Fetching events with date between {} and {}", startTime, endTime);

        return dsl.selectFrom(EVENTS)
                .where(EVENTS.START_TIME.greaterOrEqual(startTime)
                                .and(EVENTS.END_TIME.lessOrEqual(endTime))
                        )
                .fetch()
                .map(this::toDto);
    }

    public int addEvent(EventDto eventDto)
    {
        log.info("Adding event");

        return dsl.insertInto(EVENTS, EVENTS.VENUE_ID, EVENTS.NAME, EVENTS.START_TIME, EVENTS.END_TIME)
                .values(eventDto.venueId(), eventDto.name(), eventDto.startTime(), eventDto.endTime())
                .execute();
    }

    public int modifyEvent(EventDto eventDto)
    {
        log.info("Modifying event with id: {}", eventDto.eventId());

        return dsl.update(EVENTS)
                .set(EVENTS.NAME, eventDto.name())
                .set(EVENTS.START_TIME, eventDto.startTime())
                .set(EVENTS.END_TIME, eventDto.endTime())
                .where(EVENTS.EVENT_ID.eq(eventDto.eventId())).execute();
    }

    private EventDto toDto(Record r)
    {
        return EventDto.create(
                r.get(EVENTS.EVENT_ID),
                r.get(EVENTS.VENUE_ID),
                r.get(EVENTS.NAME),
                r.get(EVENTS.START_TIME),
                r.get(EVENTS.END_TIME)
        );
    }
}
