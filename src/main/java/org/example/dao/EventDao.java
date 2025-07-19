package org.example.dao;

import org.example.dto.EventDto;
import org.jooq.DSLContext;
import org.jooq.Record;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.example.jooq.generated.tables.Events.EVENTS;

public class EventDao
{
    private final DSLContext dsl;

    public EventDao(DSLContext dsl)
    {
        this.dsl = dsl;
    }

    public Optional<EventDto> findEventById(int eventId)
    {
        Record eventRecord = dsl.selectFrom(EVENTS)
                .where(EVENTS.EVENT_ID.eq(eventId))
                .fetchOne();

        return Optional.ofNullable(eventRecord)
                .map(this::toDto);
    }

    public List<EventDto> findEventsByName(String name)
    {
        return dsl.selectFrom(EVENTS)
                .where(EVENTS.NAME.eq(name))
                .fetch()
                .map(this::toDto);
    }

    public List<EventDto> findEventsByDateRange(LocalDateTime startTime, LocalDateTime endTime)
    {
        return dsl.selectFrom(EVENTS)
                .where(EVENTS.START_TIME.greaterOrEqual(startTime)
                                .and(EVENTS.END_TIME.lessOrEqual(endTime))
                        )
                .fetch()
                .map(this::toDto);
    }

    public int addEvent(EventDto eventDto)
    {
        return dsl.insertInto(EVENTS, EVENTS.VENUE_ID, EVENTS.NAME, EVENTS.START_TIME, EVENTS.END_TIME)
                .values(eventDto.venueId(), eventDto.name(), eventDto.startTime(), eventDto.endTime())
                .execute();
    }

    public int modifyEvent(EventDto eventDto)
    {
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
