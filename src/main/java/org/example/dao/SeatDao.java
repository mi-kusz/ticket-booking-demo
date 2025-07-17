package org.example.dao;

import org.example.dto.SeatDto;
import org.jooq.DSLContext;
import org.jooq.Record;

import java.util.List;
import java.util.Optional;

import static org.example.jooq.generated.tables.Seats.SEATS;

public class SeatDao
{
    private final DSLContext dsl;

    public SeatDao(DSLContext dsl)
    {
        this.dsl = dsl;
    }

    public Optional<SeatDto> findSeatById(int seatId)
    {
        Record seatRecord = dsl.selectFrom(SEATS)
                .where(SEATS.SEAT_ID.eq(seatId))
                .fetchOne();

        return Optional.ofNullable(seatRecord)
                .map(this::toDto);
    }

    public List<SeatDto> findSeatsByVenue(int venueId)
    {
        return dsl.selectFrom(SEATS)
                .where(SEATS.VENUE_ID.eq(venueId))
                .fetch()
                .map(this::toDto);
    }

    public List<SeatDto> findSeatsByVenueAndSeatRow(int venueId, String seatRow)
    {
        return dsl.selectFrom(SEATS)
                .where(SEATS.VENUE_ID.eq(venueId).and(SEATS.SEAT_ROW.eq(seatRow)))
                .fetch()
                .map(this::toDto);
    }

    public int addSeat(SeatDto seatDto)
    {
        return dsl.insertInto(SEATS, SEATS.VENUE_ID, SEATS.SEAT_ROW, SEATS.SEAT_NUMBER)
                .values(seatDto.venueId(), seatDto.seatRow(), seatDto.seatNumber())
                .execute();
    }

    private SeatDto toDto(Record r)
    {
        return SeatDto.create(
                r.get(SEATS.SEAT_ID),
                r.get(SEATS.VENUE_ID),
                r.get(SEATS.SEAT_ROW),
                r.get(SEATS.SEAT_NUMBER)
        );
    }
}
