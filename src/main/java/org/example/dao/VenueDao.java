package org.example.dao;

import org.example.dto.VenueDto;
import org.example.jooq.generated.tables.records.VenuesRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.UpdateSetFirstStep;
import org.jooq.UpdateSetMoreStep;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.example.jooq.generated.tables.Venues.VENUES;

public class VenueDao
{
    private final DSLContext dsl;

    public VenueDao(DSLContext dsl)
    {
        this.dsl = dsl;
    }

    public Optional<VenueDto> findVenueById(int venueId)
    {
        Record venueRecord = dsl.selectFrom(VENUES)
                .where(VENUES.VENUE_ID.eq(venueId))
                .fetchOne();

        return Optional.ofNullable(venueRecord)
                .map(this::toDto);
    }

    public List<VenueDto> findVenueByName(String name)
    {
        return dsl.selectFrom(VENUES)
                .where(VENUES.NAME.eq(name))
                .fetch()
                .map(this::toDto);
    }

    public List<VenueDto> findVenueByAddress(String address)
    {
        return dsl.selectFrom(VENUES)
                .where(VENUES.ADDRESS.eq(address))
                .fetch()
                .map(this::toDto);
    }

    public List<VenueDto> findVenueByNameAndAddress(String name, String address)
    {
        return dsl.selectFrom(VENUES)
                .where(VENUES.NAME.eq(name).and(VENUES.ADDRESS.eq(address)))
                .fetch()
                .map(this::toDto);
    }

    public int addVenue(VenueDto venueDto)
    {
        return dsl.insertInto(VENUES, VENUES.NAME, VENUES.ADDRESS)
                .values(venueDto.name(), venueDto.address())
                .execute();
    }

    public int modifyVenue(VenueDto venueDto)
    {
        UpdateSetFirstStep<VenuesRecord> emptyQuery = dsl.update(VENUES);
        UpdateSetMoreStep<VenuesRecord> builtQuery = null;

        if (venueDto.name() != null)
        {
            builtQuery = emptyQuery.set(VENUES.NAME, venueDto.name());
        }

        if (venueDto.address() != null)
        {
            builtQuery = Objects.requireNonNullElse(builtQuery, emptyQuery).set(VENUES.ADDRESS, venueDto.address());
        }

        if (builtQuery == null)
        {
            return 0;
        }
        else
        {
            return builtQuery.where(VENUES.VENUE_ID.eq(venueDto.venueId())).execute();
        }
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
