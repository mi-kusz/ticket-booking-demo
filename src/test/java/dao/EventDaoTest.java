package dao;

import org.example.dao.EventDao;
import org.example.dto.EventDto;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockDataProvider;
import org.jooq.tools.jdbc.MockResult;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.example.jooq.generated.tables.Events.EVENTS;
import static org.junit.jupiter.api.Assertions.*;

public class EventDaoTest
{
    private DSLContext dslFor(MockDataProvider provider)
    {
        return DSL.using(new MockConnection(provider), SQLDialect.POSTGRES);
    }

    private EventDto testEvent()
    {
        return EventDto.create(1, 1, "Test event", LocalDateTime.now(), LocalDateTime.now().plusDays(1));
    }

    private void assertEqualEvent(EventDto expected, EventDto actual)
    {
        assertEquals(expected.eventId(), actual.eventId());
        assertEquals(expected.venueId(), actual.venueId());
        assertEquals(expected.name(), actual.name());
        assertEquals(expected.startTime(), actual.startTime());
        assertEquals(expected.endTime(), actual.endTime());
    }

    @Test
    public void testFindEvents()
    {
        EventDto event = testEvent();

        MockDataProvider dataProvider = ctx -> {
            Record record = DSL.using(SQLDialect.POSTGRES).newRecord(EVENTS.fields());

            record.set(EVENTS.EVENT_ID, event.eventId());
            record.set(EVENTS.VENUE_ID, event.venueId());
            record.set(EVENTS.NAME, event.name());
            record.set(EVENTS.START_TIME, event.startTime());
            record.set(EVENTS.END_TIME, event.endTime());

            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(EVENTS.fields());
            result.add(record);

            return new MockResult[] {new MockResult(1, result)};
        };

        EventDao eventDao = new EventDao(dslFor(dataProvider));
        List<EventDto> listResult = eventDao.findEvents();

        assertEquals(1, listResult.size());

        EventDto result = listResult.getFirst();

        assertEqualEvent(event, result);
    }

    @Test
    public void testFindEventById()
    {
        EventDto event = testEvent();

        MockDataProvider dataProvider = ctx -> {
            Record record = DSL.using(SQLDialect.POSTGRES).newRecord(EVENTS.fields());

            record.set(EVENTS.EVENT_ID, event.eventId());
            record.set(EVENTS.VENUE_ID, event.venueId());
            record.set(EVENTS.NAME, event.name());
            record.set(EVENTS.START_TIME, event.startTime());
            record.set(EVENTS.END_TIME, event.endTime());

            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(EVENTS.fields());
            result.add(record);

            return new MockResult[] {new MockResult(1, result)};
        };

        EventDao eventDao = new EventDao(dslFor(dataProvider));
        Optional<EventDto> optionalResult = eventDao.findEventById(event.eventId());

        assertTrue(optionalResult.isPresent());

        EventDto result = optionalResult.get();

        assertEqualEvent(event, result);
    }

    @Test
    public void testFindNonExistingEventById()
    {
        MockDataProvider dataProvider = ctx -> {
            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult();

            return new MockResult[] {new MockResult(0, result)};
        };

        EventDao eventDao = new EventDao(dslFor(dataProvider));
        Optional<EventDto> optionalResult = eventDao.findEventById(1);

        assertFalse(optionalResult.isPresent());
    }

    @Test
    public void testFindEventByName()
    {
        EventDto event = testEvent();

        MockDataProvider dataProvider = ctx -> {
            Record record = DSL.using(SQLDialect.POSTGRES).newRecord(EVENTS.fields());

            record.set(EVENTS.EVENT_ID, event.eventId());
            record.set(EVENTS.VENUE_ID, event.venueId());
            record.set(EVENTS.NAME, event.name());
            record.set(EVENTS.START_TIME, event.startTime());
            record.set(EVENTS.END_TIME, event.endTime());

            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(EVENTS.fields());
            result.add(record);

            return new MockResult[] {new MockResult(1, result)};
        };

        EventDao eventDao = new EventDao(dslFor(dataProvider));
        List<EventDto> listResult = eventDao.findEventsByName(event.name());

        assertEquals(1, listResult.size());

        EventDto result = listResult.getFirst();

        assertEqualEvent(event, result);
    }

    @Test
    public void testFindNonExistingEventByName()
    {
        MockDataProvider dataProvider = ctx -> {
            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult();

            return new MockResult[] {new MockResult(0, result)};
        };

        EventDao eventDao = new EventDao(dslFor(dataProvider));
        List<EventDto> listResult = eventDao.findEventsByName("abc");

        assertTrue(listResult.isEmpty());
    }

    @Test
    public void testFindMultipleEventsByName()
    {
        EventDto event = testEvent();

        MockDataProvider dataProvider = ctx -> {
            Record record1 = DSL.using(SQLDialect.POSTGRES).newRecord(EVENTS.fields());
            Record record2 = DSL.using(SQLDialect.POSTGRES).newRecord(EVENTS.fields());

            record1.set(EVENTS.EVENT_ID, event.eventId());
            record1.set(EVENTS.VENUE_ID, event.venueId());
            record1.set(EVENTS.NAME, event.name());
            record1.set(EVENTS.START_TIME, event.startTime());
            record1.set(EVENTS.END_TIME, event.endTime());

            record2.set(EVENTS.EVENT_ID, 2);
            record2.set(EVENTS.VENUE_ID, 2);
            record2.set(EVENTS.NAME, event.name());
            record2.set(EVENTS.START_TIME, LocalDateTime.now().minusDays(1));
            record2.set(EVENTS.END_TIME, LocalDateTime.now().plusHours(2));

            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(EVENTS.fields());
            result.add(record1);
            result.add(record2);

            return new MockResult[] {new MockResult(2, result)};
        };

        EventDao eventDao = new EventDao(dslFor(dataProvider));
        List<EventDto> listResult = eventDao.findEventsByName(event.name());

        assertEquals(2, listResult.size());
    }

    @Test
    public void findEventByDateRange()
    {
        EventDto event = testEvent();

        MockDataProvider dataProvider = ctx -> {
            Record record = DSL.using(SQLDialect.POSTGRES).newRecord(EVENTS.fields());

            record.set(EVENTS.EVENT_ID, event.eventId());
            record.set(EVENTS.VENUE_ID, event.venueId());
            record.set(EVENTS.NAME, event.name());
            record.set(EVENTS.START_TIME, event.startTime());
            record.set(EVENTS.END_TIME, event.endTime());

            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(EVENTS.fields());
            result.add(record);

            return new MockResult[] {new MockResult(1, result)};
        };

        EventDao eventDao = new EventDao(dslFor(dataProvider));
        List<EventDto> listResult = eventDao.findEventsByDateRange(event.startTime(), event.endTime());

        assertEquals(1, listResult.size());

        EventDto result = listResult.getFirst();

        assertEqualEvent(event, result);
    }

    @Test
    public void findNonExistingEventByDateRange()
    {
        MockDataProvider dataProvider = ctx -> {
            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult();

            return new MockResult[] {new MockResult(0, result)};
        };

        EventDao eventDao = new EventDao(dslFor(dataProvider));
        List<EventDto> listResult = eventDao.findEventsByDateRange(LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        assertTrue(listResult.isEmpty());
    }

    @Test
    public void findMultipleEventsByDateRange()
    {
        EventDto event = testEvent();

        MockDataProvider dataProvider = ctx -> {
            Record record1 = DSL.using(SQLDialect.POSTGRES).newRecord(EVENTS.fields());
            Record record2 = DSL.using(SQLDialect.POSTGRES).newRecord(EVENTS.fields());

            record1.set(EVENTS.EVENT_ID, event.eventId());
            record1.set(EVENTS.VENUE_ID, event.venueId());
            record1.set(EVENTS.NAME, event.name());
            record1.set(EVENTS.START_TIME, event.startTime());
            record1.set(EVENTS.END_TIME, event.endTime());

            record2.set(EVENTS.EVENT_ID, 2);
            record2.set(EVENTS.VENUE_ID, 2);
            record2.set(EVENTS.NAME, "abcd");
            record2.set(EVENTS.START_TIME, event.startTime());
            record2.set(EVENTS.END_TIME, event.endTime());

            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(EVENTS.fields());
            result.add(record1);
            result.add(record2);

            return new MockResult[] {new MockResult(2, result)};
        };

        EventDao eventDao = new EventDao(dslFor(dataProvider));
        List<EventDto> listResult = eventDao.findEventsByDateRange(event.startTime(), event.endTime());

        assertEquals(2, listResult.size());
    }

    @Test
    public void testAddEvent()
    {
        EventDto event = testEvent();

        MockDataProvider dataProvider = ctx -> new MockResult[] { new MockResult(1, null) };

        EventDao eventDao = new EventDao(dslFor(dataProvider));
        int affected = eventDao.addEvent(event);

        assertEquals(1, affected);
    }

    @Test
    public void testAddEventError()
    {
        EventDto event = testEvent();

        MockDataProvider dataProvider = ctx -> new MockResult[] { new MockResult(0, null) };

        EventDao eventDao = new EventDao(dslFor(dataProvider));
        int affected = eventDao.addEvent(event);

        assertEquals(0, affected);
    }

    @Test
    public void testModifyEvent()
    {
        EventDto event = testEvent();

        MockDataProvider dataProvider = ctx -> new MockResult[] { new MockResult(1, null) };

        EventDao dao = new EventDao(dslFor(dataProvider));
        int affected = dao.modifyEvent(event);

        assertEquals(1, affected);
    }
}
