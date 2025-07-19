package dao;

import org.example.dao.EventDao;
import org.example.dao.SeatDao;
import org.example.dto.EventDto;
import org.example.dto.SeatDto;
import static org.example.jooq.generated.tables.Seats.*;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockDataProvider;
import org.jooq.tools.jdbc.MockResult;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class SeatDaoTest
{
    private DSLContext dslFor(MockDataProvider provider)
    {
        return DSL.using(new MockConnection(provider), SQLDialect.POSTGRES);
    }

    private SeatDto testSeat()
    {
        return SeatDto.create(1, 1, "A", 1);
    }

    private void assertEqualSeat(SeatDto expected, SeatDto actual)
    {
        assertEquals(expected.seatId(), actual.seatId());
        assertEquals(expected.venueId(), actual.venueId());
        assertEquals(expected.seatRow(), actual.seatRow());
        assertEquals(expected.seatNumber(), actual.seatNumber());
    }

    @Test
    public void testFindSeatById()
    {
        SeatDto seat = testSeat();

        MockDataProvider dataProvider = ctx -> {
            Record record = DSL.using(SQLDialect.POSTGRES).newRecord(SEATS.fields());

            record.set(SEATS.SEAT_ID, seat.seatId());
            record.set(SEATS.VENUE_ID, seat.venueId());
            record.set(SEATS.SEAT_ROW, seat.seatRow());
            record.set(SEATS.SEAT_NUMBER, seat.seatNumber());

            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(SEATS.fields());
            result.add(record);

            return new MockResult[] {new MockResult(1, result)};
        };

        SeatDao seatDao = new SeatDao(dslFor(dataProvider));
        Optional<SeatDto> optionalResult = seatDao.findSeatById(seat.seatId());

        assertTrue(optionalResult.isPresent());

        SeatDto result = optionalResult.get();

        assertEqualSeat(seat, result);
    }

    @Test
    public void testFindNonExistingSeatById()
    {
        MockDataProvider dataProvider = ctx -> {
            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult();

            return new MockResult[] {new MockResult(0, result)};
        };

        SeatDao seatDao = new SeatDao(dslFor(dataProvider));
        Optional<SeatDto> optionalResult = seatDao.findSeatById(1);

        assertFalse(optionalResult.isPresent());
    }

    @Test
    public void testFindSeatByVenueId()
    {
        SeatDto seat = testSeat();

        MockDataProvider dataProvider = ctx -> {
            Record record = DSL.using(SQLDialect.POSTGRES).newRecord(SEATS.fields());

            record.set(SEATS.SEAT_ID, seat.seatId());
            record.set(SEATS.VENUE_ID, seat.venueId());
            record.set(SEATS.SEAT_ROW, seat.seatRow());
            record.set(SEATS.SEAT_NUMBER, seat.seatNumber());

            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(SEATS.fields());
            result.add(record);

            return new MockResult[] {new MockResult(1, result)};
        };

        SeatDao seatDao = new SeatDao(dslFor(dataProvider));
        List<SeatDto> resultList = seatDao.findSeatsByVenue(seat.venueId());

        assertEquals(1, resultList.size());

        SeatDto result = resultList.getFirst();

        assertEqualSeat(seat, result);
    }

    @Test
    public void testFindNonExistingSeatByVenueId()
    {
        MockDataProvider dataProvider = ctx -> {
            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult();

            return new MockResult[] {new MockResult(0, result)};
        };

        SeatDao seatDao = new SeatDao(dslFor(dataProvider));
        List<SeatDto> resultList = seatDao.findSeatsByVenue(1);

        assertTrue(resultList.isEmpty());
    }

    @Test
    public void testFindMultipleSeatsByVenueId()
    {
        SeatDto seat = testSeat();

        MockDataProvider dataProvider = ctx -> {
            Record record1 = DSL.using(SQLDialect.POSTGRES).newRecord(SEATS.fields());
            Record record2 = DSL.using(SQLDialect.POSTGRES).newRecord(SEATS.fields());

            record1.set(SEATS.SEAT_ID, seat.seatId());
            record1.set(SEATS.VENUE_ID, seat.venueId());
            record1.set(SEATS.SEAT_ROW, seat.seatRow());
            record1.set(SEATS.SEAT_NUMBER, seat.seatNumber());

            record2.set(SEATS.SEAT_ID, 2);
            record2.set(SEATS.VENUE_ID, seat.venueId());
            record2.set(SEATS.SEAT_ROW, "B");
            record2.set(SEATS.SEAT_NUMBER, 10);

            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(SEATS.fields());
            result.add(record1);
            result.add(record2);

            return new MockResult[] {new MockResult(2,  result)};
        };

        SeatDao seatDao = new SeatDao(dslFor(dataProvider));
        List<SeatDto> resultList = seatDao.findSeatsByVenue(seat.venueId());

        assertEquals(2, resultList.size());
    }

    @Test
    public void testFindSeatByVenueIdAndSeatRow()
    {
        SeatDto seat = testSeat();

        MockDataProvider dataProvider = ctx -> {
            Record record = DSL.using(SQLDialect.POSTGRES).newRecord(SEATS.fields());

            record.set(SEATS.SEAT_ID, seat.seatId());
            record.set(SEATS.VENUE_ID, seat.venueId());
            record.set(SEATS.SEAT_ROW, seat.seatRow());
            record.set(SEATS.SEAT_NUMBER, seat.seatNumber());

            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(SEATS.fields());
            result.add(record);

            return new MockResult[] {new MockResult(1, result)};
        };

        SeatDao seatDao = new SeatDao(dslFor(dataProvider));
        List<SeatDto> resultList = seatDao.findSeatsByVenueAndSeatRow(seat.venueId(), seat.seatRow());

        assertEquals(1, resultList.size());

        SeatDto result = resultList.getFirst();

        assertEqualSeat(seat, result);
    }

    @Test
    public void testFindNonExistingSeatByVenueIdAndSeatRow()
    {
        MockDataProvider dataProvider = ctx -> {
            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult();

            return new MockResult[] {new MockResult(0, result)};
        };

        SeatDao seatDao = new SeatDao(dslFor(dataProvider));
        List<SeatDto> resultList = seatDao.findSeatsByVenueAndSeatRow(1, "A");

        assertTrue(resultList.isEmpty());
    }

    @Test
    public void testFindMultipleSeatsByVenueIdAndSeatRow()
    {
        SeatDto seat = testSeat();

        MockDataProvider dataProvider = ctx -> {
            Record record1 = DSL.using(SQLDialect.POSTGRES).newRecord(SEATS.fields());
            Record record2 = DSL.using(SQLDialect.POSTGRES).newRecord(SEATS.fields());

            record1.set(SEATS.SEAT_ID, seat.seatId());
            record1.set(SEATS.VENUE_ID, seat.venueId());
            record1.set(SEATS.SEAT_ROW, seat.seatRow());
            record1.set(SEATS.SEAT_NUMBER, seat.seatNumber());

            record2.set(SEATS.SEAT_ID, 2);
            record2.set(SEATS.VENUE_ID, seat.venueId());
            record2.set(SEATS.SEAT_ROW, seat.seatRow());
            record2.set(SEATS.SEAT_NUMBER, 10);

            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(SEATS.fields());
            result.add(record1);
            result.add(record2);

            return new MockResult[] {new MockResult(2,  result)};
        };

        SeatDao seatDao = new SeatDao(dslFor(dataProvider));
        List<SeatDto> resultList = seatDao.findSeatsByVenueAndSeatRow(seat.venueId(), seat.seatRow());

        assertEquals(2, resultList.size());
    }

    @Test
    public void testAddSeat()
    {
        SeatDto seat = testSeat();

        MockDataProvider dataProvider = ctx -> new MockResult[] { new MockResult(1, null) };

        SeatDao dao = new SeatDao(dslFor(dataProvider));
        int affected = dao.addSeat(seat);

        assertEquals(1, affected);
    }

    @Test
    public void testAddSeatError()
    {
        SeatDto seat = testSeat();

        MockDataProvider dataProvider = ctx -> new MockResult[] { new MockResult(0, null) };

        SeatDao dao = new SeatDao(dslFor(dataProvider));
        int affected = dao.addSeat(seat);

        assertEquals(0, affected);
    }

    @Test
    public void testModifySeat()
    {
        SeatDto seat = testSeat();

        MockDataProvider dataProvider = ctx -> new MockResult[] { new MockResult(1, null) };

        SeatDao dao = new SeatDao(dslFor(dataProvider));
        int affected = dao.modifySeat(seat);

        assertEquals(1, affected);
    }
}
