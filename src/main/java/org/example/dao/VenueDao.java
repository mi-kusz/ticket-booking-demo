package org.example.dao;

import org.example.dto.VenueDto;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static org.example.jooq.generated.tables.Venues.VENUES;

public class VenueDao
{
    private static final Logger log = LoggerFactory.getLogger(VenueDao.class);
    private final DSLContext dsl;

    public VenueDao(DSLContext dsl)
    {
        this.dsl = dsl;
    }

    public List<VenueDto> findVenues()
    {
        log.info("Fetching all venues");

        return dsl.selectFrom(VENUES)
                .fetch()
                .map(this::toDto);
    }

    public Optional<VenueDto> findVenueById(int venueId)
    {
        log.info("Fetching venue with id: {}", venueId);

        Record venueRecord = dsl.selectFrom(VENUES)
                .where(VENUES.VENUE_ID.eq(venueId))
                .fetchOne();

        return Optional.ofNullable(venueRecord)
                .map(this::toDto);
    }

    public List<VenueDto> findVenueByName(String name)
    {
        log.info("Fetching venues with name: {}", name);

        return dsl.selectFrom(VENUES)
                .where(VENUES.NAME.eq(name))
                .fetch()
                .map(this::toDto);
    }

    public List<VenueDto> findVenueByAddress(String address)
    {
        log.info("Fetching venues with address: {}", address);

        return dsl.selectFrom(VENUES)
                .where(VENUES.ADDRESS.eq(address))
                .fetch()
                .map(this::toDto);
    }

    public List<VenueDto> findVenueByNameAndAddress(String name, String address)
    {
        log.info("Fetching venues with name: {} and address: {}", name, address);

        return dsl.selectFrom(VENUES)
                .where(VENUES.NAME.eq(name).and(VENUES.ADDRESS.eq(address)))
                .fetch()
                .map(this::toDto);
    }

    public int addVenue(VenueDto venueDto)
    {
        log.info("Adding venue");

        return dsl.insertInto(VENUES, VENUES.NAME, VENUES.ADDRESS)
                .values(venueDto.name(), venueDto.address())
                .execute();
    }

    public int modifyVenue(VenueDto venueDto)
    {
        log.info("Modifying venue with id: {}", venueDto.venueId());

        return dsl.update(VENUES)
                .set(VENUES.NAME, venueDto.name())
                .set(VENUES.ADDRESS, venueDto.address())
                .where(VENUES.VENUE_ID.eq(venueDto.venueId()))
                .execute();
    }

    private VenueDto toDto(Record r)
    {
        return VenueDto.create(
                r.get(VENUES.VENUE_ID),
                r.get(VENUES.NAME),
                r.get(VENUES.ADDRESS)
        );
    }
}
