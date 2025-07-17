package org.example.dao;

import org.example.dto.EventDto;
import org.example.jooq.generated.tables.records.EventsRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.UpdateSetFirstStep;
import org.jooq.UpdateSetMoreStep;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
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
        UpdateSetFirstStep<EventsRecord> emptyQuery = dsl.update(EVENTS);
        UpdateSetMoreStep<EventsRecord> builtQuery = null;

        if (eventDto.name() != null)
        {
            builtQuery = emptyQuery.set(EVENTS.NAME, eventDto.name());
        }

        if (eventDto.startTime() != null)
        {
            builtQuery = Objects.requireNonNullElse(builtQuery, emptyQuery).set(EVENTS.START_TIME, eventDto.startTime());
        }

        if (eventDto.endTime() != null)
        {
            builtQuery = Objects.requireNonNullElse(builtQuery, emptyQuery).set(EVENTS.END_TIME, eventDto.endTime());
        }

        if (builtQuery == null)
        {
            return 0;
        }
        else
        {
            return builtQuery.where(EVENTS.EVENT_ID.eq(eventDto.eventId())).execute();
        }
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
