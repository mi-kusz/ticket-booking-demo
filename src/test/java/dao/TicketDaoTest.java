package dao;

import org.example.dao.TicketDao;
import org.example.dto.TicketDto;
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

import static org.example.jooq.generated.tables.Tickets.TICKETS;
import static org.junit.jupiter.api.Assertions.*;

public class TicketDaoTest
{
    private DSLContext dslFor(MockDataProvider provider)
    {
        return DSL.using(new MockConnection(provider), SQLDialect.POSTGRES);
    }

    private TicketDto testTicket()
    {
        return TicketDto.create(1, 1, 1, 1, LocalDateTime.now());
    }

    private void assertEqualTicket(TicketDto expected, TicketDto actual)
    {
        assertEquals(expected.userId(), actual.ticketId());
        assertEquals(expected.eventId(), actual.eventId());
        assertEquals(expected.seatId(), actual.seatId());
        assertEquals(expected.userId(), actual.userId());
        assertEquals(expected.bookedAt(), actual.bookedAt());
    }

    @Test
    public void testFindTickets()
    {
        TicketDto ticket = testTicket();

        MockDataProvider dataProvider = ctx -> {
            Record record = DSL.using(SQLDialect.POSTGRES).newRecord(TICKETS.fields());

            record.set(TICKETS.TICKET_ID, ticket.ticketId());
            record.set(TICKETS.EVENT_ID, ticket.eventId());
            record.set(TICKETS.SEAT_ID, ticket.seatId());
            record.set(TICKETS.USER_ID, ticket.userId());
            record.set(TICKETS.BOOKED_AT, ticket.bookedAt());

            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(TICKETS.fields());
            result.add(record);

            return new MockResult[] {new MockResult(1, result)};
        };

        TicketDao ticketDao = new TicketDao(dslFor(dataProvider));
        List<TicketDto> resultList = ticketDao.findTickets();

        assertEquals(1, resultList.size());

        TicketDto result = resultList.getFirst();

        assertEqualTicket(ticket, result);
    }

    @Test
    public void testFindTicketById()
    {
        TicketDto ticket = testTicket();

        MockDataProvider dataProvider = ctx -> {
            Record record = DSL.using(SQLDialect.POSTGRES).newRecord(TICKETS.fields());

            record.set(TICKETS.TICKET_ID, ticket.ticketId());
            record.set(TICKETS.EVENT_ID, ticket.eventId());
            record.set(TICKETS.SEAT_ID, ticket.seatId());
            record.set(TICKETS.USER_ID, ticket.userId());
            record.set(TICKETS.BOOKED_AT, ticket.bookedAt());

            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(TICKETS.fields());
            result.add(record);

            return new MockResult[] {new MockResult(1, result)};
        };

        TicketDao ticketDao = new TicketDao(dslFor(dataProvider));
        Optional<TicketDto> optionalResult = ticketDao.findTicketById(ticket.ticketId());

        assertTrue(optionalResult.isPresent());

        TicketDto result = optionalResult.get();

        assertEqualTicket(ticket, result);
    }

    @Test
    public void testFindNonExistingTicketById()
    {
        MockDataProvider dataProvider = ctx -> {
            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(TICKETS.fields());

            return new MockResult[] {new MockResult(0, result)};
        };

        TicketDao ticketDao = new TicketDao(dslFor(dataProvider));
        Optional<TicketDto> optionalResult = ticketDao.findTicketById(1);

        assertFalse(optionalResult.isPresent());
    }

    @Test
    public void testFindTicketByEventId()
    {
        TicketDto ticket = testTicket();

        MockDataProvider dataProvider = ctx -> {
            Record record = DSL.using(SQLDialect.POSTGRES).newRecord(TICKETS.fields());

            record.set(TICKETS.TICKET_ID, ticket.ticketId());
            record.set(TICKETS.EVENT_ID, ticket.eventId());
            record.set(TICKETS.SEAT_ID, ticket.seatId());
            record.set(TICKETS.USER_ID, ticket.userId());
            record.set(TICKETS.BOOKED_AT, ticket.bookedAt());

            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(TICKETS.fields());
            result.add(record);

            return new MockResult[] {new MockResult(1, result)};
        };

        TicketDao ticketDao = new TicketDao(dslFor(dataProvider));
        List<TicketDto> resultList = ticketDao.findTicketsByEventId(ticket.eventId());

        assertEquals(1, resultList.size());

        TicketDto result = resultList.getFirst();

        assertEqualTicket(ticket, result);
    }

    @Test
    public void testFindNonExistingTicketByEventId()
    {
        MockDataProvider dataProvider = ctx -> {
            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(TICKETS.fields());

            return new MockResult[] {new MockResult(0, result)};
        };

        TicketDao ticketDao = new TicketDao(dslFor(dataProvider));
        List<TicketDto> resultList = ticketDao.findTicketsByEventId(1);

        assertTrue(resultList.isEmpty());
    }

    @Test
    public void testFindMultipleTicketsByEventId()
    {
        TicketDto ticket = testTicket();

        MockDataProvider dataProvider = ctx -> {
            Record record1 = DSL.using(SQLDialect.POSTGRES).newRecord(TICKETS.fields());
            Record record2 = DSL.using(SQLDialect.POSTGRES).newRecord(TICKETS.fields());

            record1.set(TICKETS.TICKET_ID, ticket.ticketId());
            record1.set(TICKETS.EVENT_ID, ticket.eventId());
            record1.set(TICKETS.SEAT_ID, ticket.seatId());
            record1.set(TICKETS.USER_ID, ticket.userId());
            record1.set(TICKETS.BOOKED_AT, ticket.bookedAt());

            record2.set(TICKETS.TICKET_ID, 2);
            record2.set(TICKETS.EVENT_ID, ticket.eventId());
            record2.set(TICKETS.SEAT_ID, 2);
            record2.set(TICKETS.USER_ID, 2);
            record2.set(TICKETS.BOOKED_AT, ticket.bookedAt().plusHours(1));

            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(TICKETS.fields());
            result.add(record1);
            result.add(record2);

            return new MockResult[] {new MockResult(2, result)};
        };

        TicketDao ticketDao = new TicketDao(dslFor(dataProvider));
        List<TicketDto> resultList = ticketDao.findTicketsByEventId(ticket.eventId());

        assertEquals(2, resultList.size());
    }

    @Test
    public void testFindTicketByUserId()
    {
        TicketDto ticket = testTicket();

        MockDataProvider dataProvider = ctx -> {
            Record record = DSL.using(SQLDialect.POSTGRES).newRecord(TICKETS.fields());

            record.set(TICKETS.TICKET_ID, ticket.ticketId());
            record.set(TICKETS.EVENT_ID, ticket.eventId());
            record.set(TICKETS.SEAT_ID, ticket.seatId());
            record.set(TICKETS.USER_ID, ticket.userId());
            record.set(TICKETS.BOOKED_AT, ticket.bookedAt());

            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(TICKETS.fields());
            result.add(record);

            return new MockResult[] {new MockResult(1, result)};
        };

        TicketDao ticketDao = new TicketDao(dslFor(dataProvider));
        List<TicketDto> resultList = ticketDao.findTicketsByUserId(ticket.userId());

        assertEquals(1, resultList.size());

        TicketDto result = resultList.getFirst();

        assertEqualTicket(ticket, result);
    }

    @Test
    public void testFindNonExistingTicketByUserId()
    {
        MockDataProvider dataProvider = ctx -> {
            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(TICKETS.fields());

            return new MockResult[] {new MockResult(0, result)};
        };

        TicketDao ticketDao = new TicketDao(dslFor(dataProvider));
        List<TicketDto> resultList = ticketDao.findTicketsByUserId(1);

        assertTrue(resultList.isEmpty());
    }

    @Test
    public void testFindMultipleTicketsByUserId()
    {
        TicketDto ticket = testTicket();

        MockDataProvider dataProvider = ctx -> {
            Record record1 = DSL.using(SQLDialect.POSTGRES).newRecord(TICKETS.fields());
            Record record2 = DSL.using(SQLDialect.POSTGRES).newRecord(TICKETS.fields());

            record1.set(TICKETS.TICKET_ID, ticket.ticketId());
            record1.set(TICKETS.EVENT_ID, ticket.eventId());
            record1.set(TICKETS.SEAT_ID, ticket.seatId());
            record1.set(TICKETS.USER_ID, ticket.userId());
            record1.set(TICKETS.BOOKED_AT, ticket.bookedAt());

            record2.set(TICKETS.TICKET_ID, 2);
            record2.set(TICKETS.EVENT_ID, 2);
            record2.set(TICKETS.SEAT_ID, 2);
            record2.set(TICKETS.USER_ID, ticket.userId());
            record2.set(TICKETS.BOOKED_AT, ticket.bookedAt().plusHours(1));

            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(TICKETS.fields());
            result.add(record1);
            result.add(record2);

            return new MockResult[] {new MockResult(2, result)};
        };

        TicketDao ticketDao = new TicketDao(dslFor(dataProvider));
        List<TicketDto> resultList = ticketDao.findTicketsByUserId(ticket.userId());

        assertEquals(2, resultList.size());
    }

    @Test
    public void testFindTicketByBookedDate()
    {
        TicketDto ticket = testTicket();

        MockDataProvider dataProvider = ctx -> {
            Record record = DSL.using(SQLDialect.POSTGRES).newRecord(TICKETS.fields());

            record.set(TICKETS.TICKET_ID, ticket.ticketId());
            record.set(TICKETS.EVENT_ID, ticket.eventId());
            record.set(TICKETS.SEAT_ID, ticket.seatId());
            record.set(TICKETS.USER_ID, ticket.userId());
            record.set(TICKETS.BOOKED_AT, ticket.bookedAt());

            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(TICKETS.fields());
            result.add(record);

            return new MockResult[] {new MockResult(1, result)};
        };

        TicketDao ticketDao = new TicketDao(dslFor(dataProvider));
        List<TicketDto> resultList = ticketDao.findTicketsByBookedDate(ticket.bookedAt(), ticket.bookedAt().plusDays(5));

        assertEquals(1, resultList.size());

        TicketDto result = resultList.getFirst();

        assertEqualTicket(ticket, result);
    }

    @Test
    public void testFindNonExistingTicketByBookedDate()
    {
        MockDataProvider dataProvider = ctx -> {
            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(TICKETS.fields());

            return new MockResult[] {new MockResult(0, result)};
        };

        TicketDao ticketDao = new TicketDao(dslFor(dataProvider));
        List<TicketDto> resultList = ticketDao.findTicketsByBookedDate(LocalDateTime.now(), LocalDateTime.now().plusDays(5));

        assertEquals(0, resultList.size());
    }

    @Test
    public void testFindMultipleTicketsByBookedDate()
    {
        TicketDto ticket = testTicket();

        MockDataProvider dataProvider = ctx -> {
            Record record1 = DSL.using(SQLDialect.POSTGRES).newRecord(TICKETS.fields());
            Record record2 = DSL.using(SQLDialect.POSTGRES).newRecord(TICKETS.fields());

            record1.set(TICKETS.TICKET_ID, ticket.ticketId());
            record1.set(TICKETS.EVENT_ID, ticket.eventId());
            record1.set(TICKETS.SEAT_ID, ticket.seatId());
            record1.set(TICKETS.USER_ID, ticket.userId());
            record1.set(TICKETS.BOOKED_AT, ticket.bookedAt());

            record2.set(TICKETS.TICKET_ID, 2);
            record2.set(TICKETS.EVENT_ID, 2);
            record2.set(TICKETS.SEAT_ID, 2);
            record2.set(TICKETS.USER_ID, 2);
            record2.set(TICKETS.BOOKED_AT, ticket.bookedAt());

            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(TICKETS.fields());
            result.add(record1);
            result.add(record2);

            return new MockResult[] {new MockResult(2, result)};
        };

        TicketDao ticketDao = new TicketDao(dslFor(dataProvider));
        List<TicketDto> resultList = ticketDao.findTicketsByBookedDate(ticket.bookedAt(), ticket.bookedAt().plusDays(5));

        assertEquals(2, resultList.size());
    }

    @Test
    public void testAddTicket()
    {
        TicketDto ticket = testTicket();

        MockDataProvider dataProvider = ctx -> new MockResult[] { new MockResult(1, null) };

        TicketDao dao = new TicketDao(dslFor(dataProvider));
        int affected = dao.addTicket(ticket);

        assertEquals(1, affected);
    }

    @Test
    public void testAddTicketError()
    {
        TicketDto ticket = testTicket();

        MockDataProvider dataProvider = ctx -> new MockResult[] { new MockResult(1, null) };

        TicketDao dao = new TicketDao(dslFor(dataProvider));
        int affected = dao.addTicket(ticket);

        assertEquals(1, affected);
    }

    @Test
    public void testModifyTicket()
    {
        TicketDto ticket = testTicket();

        MockDataProvider dataProvider = ctx -> new MockResult[] { new MockResult(1, null) };

        TicketDao dao = new TicketDao(dslFor(dataProvider));
        int affected = dao.modifyTicket(ticket);

        assertEquals(1, affected);
    }
}
