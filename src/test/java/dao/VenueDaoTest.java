package dao;

import org.example.dao.VenueDao;
import org.example.dto.VenueDto;
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

import static org.example.jooq.generated.tables.Venues.VENUES;
import static org.junit.jupiter.api.Assertions.*;

public class VenueDaoTest
{
    private DSLContext dslFor(MockDataProvider provider)
    {
        return DSL.using(new MockConnection(provider), SQLDialect.POSTGRES);
    }

    private VenueDto testVenue()
    {
        return VenueDto.create(1, "Test name", "Test address");
    }

    private void assertEqualVenue(VenueDto expected, VenueDto actual)
    {
        assertEquals(expected.venueId(), actual.venueId());
        assertEquals(expected.name(), actual.name());
        assertEquals(expected.address(), actual.address());
    }

    @Test
    public void testFindVenueById()
    {
        VenueDto venue = testVenue();

        MockDataProvider dataProvider = ctx -> {
            Record record = DSL.using(SQLDialect.POSTGRES).newRecord(VENUES.fields());

            record.set(VENUES.VENUE_ID, venue.venueId());
            record.set(VENUES.NAME, venue.name());
            record.set(VENUES.ADDRESS, venue.address());

            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(VENUES.fields());
            result.add(record);

            return new MockResult[] {new MockResult(1, result)};
        };

        VenueDao venueDao = new VenueDao(dslFor(dataProvider));
        Optional<VenueDto> optionalResult = venueDao.findVenueById(venue.venueId());

        assertTrue(optionalResult.isPresent());

        VenueDto result = optionalResult.get();

        assertEqualVenue(venue, result);
    }

    @Test
    public void testFindNonExistingVenueById()
    {
        MockDataProvider dataProvider = ctx -> {
            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult();

            return new MockResult[] {new MockResult(0, result)};
        };

        VenueDao venueDao = new VenueDao(dslFor(dataProvider));
        Optional<VenueDto> optionalResult = venueDao.findVenueById(1);

        assertFalse(optionalResult.isPresent());
    }

    @Test
    public void testFindVenueByName()
    {
        VenueDto venue = testVenue();

        MockDataProvider dataProvider = ctx -> {
            Record record = DSL.using(SQLDialect.POSTGRES).newRecord(VENUES.fields());

            record.set(VENUES.VENUE_ID, venue.venueId());
            record.set(VENUES.NAME, venue.name());
            record.set(VENUES.ADDRESS, venue.address());

            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(VENUES.fields());
            result.add(record);

            return new MockResult[] {new MockResult(1, result)};
        };

        VenueDao venueDao = new VenueDao(dslFor(dataProvider));
        List<VenueDto> resultList = venueDao.findVenueByName(venue.name());

        assertEquals(1, resultList.size());

        VenueDto result = resultList.getFirst();

        assertEqualVenue(venue, result);
    }


    @Test
    public void testFindNonExistingVenueByName()
    {
        MockDataProvider dataProvider = ctx -> {

            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult();

            return new MockResult[] {new MockResult(0, result)};
        };

        VenueDao venueDao = new VenueDao(dslFor(dataProvider));
        List<VenueDto> resultList = venueDao.findVenueByName("Test name");

        assertTrue(resultList.isEmpty());
    }

    @Test
    public void testFindMultipleVenuesByName()
    {
        VenueDto venue = testVenue();

        MockDataProvider dataProvider = ctx -> {
            Record record1 = DSL.using(SQLDialect.POSTGRES).newRecord(VENUES.fields());
            Record record2 = DSL.using(SQLDialect.POSTGRES).newRecord(VENUES.fields());

            record1.set(VENUES.VENUE_ID, venue.venueId());
            record1.set(VENUES.NAME, venue.name());
            record1.set(VENUES.ADDRESS, venue.address());

            record2.set(VENUES.VENUE_ID, 2);
            record2.set(VENUES.NAME, venue.name());
            record2.set(VENUES.ADDRESS, "Address");

            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(VENUES.fields());
            result.add(record1);
            result.add(record2);

            return new MockResult[] {new MockResult(2, result)};
        };

        VenueDao venueDao = new VenueDao(dslFor(dataProvider));
        List<VenueDto> resultList = venueDao.findVenueByName(venue.name());

        assertEquals(2, resultList.size());
    }

    @Test
    public void testFindVenueByAddress()
    {
        VenueDto venue = testVenue();

        MockDataProvider dataProvider = ctx -> {
            Record record = DSL.using(SQLDialect.POSTGRES).newRecord(VENUES.fields());

            record.set(VENUES.VENUE_ID, venue.venueId());
            record.set(VENUES.NAME, venue.name());
            record.set(VENUES.ADDRESS, venue.address());

            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(VENUES.fields());
            result.add(record);

            return new MockResult[] {new MockResult(1, result)};
        };

        VenueDao venueDao = new VenueDao(dslFor(dataProvider));
        List<VenueDto> resultList = venueDao.findVenueByAddress(venue.address());

        assertEquals(1, resultList.size());

        VenueDto result = resultList.getFirst();

        assertEqualVenue(venue, result);
    }

    @Test
    public void testFindNonExistingVenueByAddress()
    {
        MockDataProvider dataProvider = ctx ->
        {
            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult();

            return new MockResult[]{new MockResult(0, result)};
        };

        VenueDao venueDao = new VenueDao(dslFor(dataProvider));
        List<VenueDto> resultList = venueDao.findVenueByAddress("Test address");

        assertTrue(resultList.isEmpty());
    }

    @Test
    public void testFindMultipleVenuesByAddress()
    {
        VenueDto venue = testVenue();

        MockDataProvider dataProvider = ctx -> {
            Record record1 = DSL.using(SQLDialect.POSTGRES).newRecord(VENUES.fields());
            Record record2 = DSL.using(SQLDialect.POSTGRES).newRecord(VENUES.fields());

            record1.set(VENUES.VENUE_ID, venue.venueId());
            record1.set(VENUES.NAME, venue.name());
            record1.set(VENUES.ADDRESS, venue.address());

            record2.set(VENUES.VENUE_ID, 2);
            record2.set(VENUES.NAME, "Name");
            record2.set(VENUES.ADDRESS, venue.address());

            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(VENUES.fields());
            result.add(record1);
            result.add(record2);

            return new MockResult[] {new MockResult(2, result)};
        };

        VenueDao venueDao = new VenueDao(dslFor(dataProvider));
        List<VenueDto> resultList = venueDao.findVenueByAddress(venue.address());

        assertEquals(2, resultList.size());
    }

    @Test
    public void testFindVenueByNameAndAddress()
    {
        VenueDto venue = testVenue();

        MockDataProvider dataProvider = ctx -> {
            Record record = DSL.using(SQLDialect.POSTGRES).newRecord(VENUES.fields());

            record.set(VENUES.VENUE_ID, venue.venueId());
            record.set(VENUES.NAME, venue.name());
            record.set(VENUES.ADDRESS, venue.address());

            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(VENUES.fields());
            result.add(record);

            return new MockResult[] {new MockResult(1, result)};
        };

        VenueDao venueDao = new VenueDao(dslFor(dataProvider));
        List<VenueDto> resultList = venueDao.findVenueByNameAndAddress(venue.name(), venue.address());

        assertEquals(1, resultList.size());

        VenueDto result = resultList.getFirst();

        assertEqualVenue(venue, result);
    }

    @Test
    public void testFindNonExistingVenueByNameAndAddress()
    {
        MockDataProvider dataProvider = ctx -> {
            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(VENUES.fields());

            return new MockResult[] {new MockResult(0, result)};
        };

        VenueDao venueDao = new VenueDao(dslFor(dataProvider));
        List<VenueDto> resultList = venueDao.findVenueByNameAndAddress("Test name", "Test address");

        assertTrue(resultList.isEmpty());
    }

    @Test
    public void testFindMultipleVenuesByNameAndAddress()
    {
        VenueDto venue = testVenue();

        MockDataProvider dataProvider = ctx -> {
            Record record1 = DSL.using(SQLDialect.POSTGRES).newRecord(VENUES.fields());
            Record record2 = DSL.using(SQLDialect.POSTGRES).newRecord(VENUES.fields());

            record1.set(VENUES.VENUE_ID, venue.venueId());
            record1.set(VENUES.NAME, venue.name());
            record1.set(VENUES.ADDRESS, venue.address());

            record2.set(VENUES.VENUE_ID, 2);
            record2.set(VENUES.NAME, venue.address());
            record2.set(VENUES.ADDRESS, venue.address());

            Result<Record> result = DSL.using(SQLDialect.POSTGRES).newResult(VENUES.fields());
            result.add(record1);
            result.add(record2);

            return new MockResult[] {new MockResult(2, result)};
        };

        VenueDao venueDao = new VenueDao(dslFor(dataProvider));
        List<VenueDto> resultList = venueDao.findVenueByNameAndAddress(venue.name(), venue.address());

        assertEquals(2, resultList.size());
    }

    @Test
    public void testAddVenue()
    {
        VenueDto venue = testVenue();

        MockDataProvider dataProvider = ctx -> new MockResult[] { new MockResult(1, null) };

        VenueDao dao = new VenueDao(dslFor(dataProvider));
        int affected = dao.addVenue(venue);

        assertEquals(1, affected);
    }

    @Test
    public void testAddVenueError()
    {
        VenueDto venue = testVenue();

        MockDataProvider dataProvider = ctx -> new MockResult[] { new MockResult(0, null) };

        VenueDao dao = new VenueDao(dslFor(dataProvider));
        int affected = dao.addVenue(venue);

        assertEquals(0, affected);
    }

    @Test
    public void testModifyVenue()
    {
        VenueDto venue = testVenue();

        MockDataProvider dataProvider = ctx -> new MockResult[] { new MockResult(1, null) };

        VenueDao dao = new VenueDao(dslFor(dataProvider));
        int affected = dao.modifyVenue(venue);

        assertEquals(1, affected);
    }
}
