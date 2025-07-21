package org.example.dao;

import org.example.dto.SeatDto;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static org.example.jooq.generated.tables.Seats.SEATS;

public class SeatDao
{
    private static final Logger log = LoggerFactory.getLogger(SeatDao.class);
    private final DSLContext dsl;

    public SeatDao(DSLContext dsl)
    {
        this.dsl = dsl;
    }

    public List<SeatDto> findSeats()
    {
        log.info("Fetching all seats");

        return dsl.selectFrom(SEATS)
                .fetch()
                .map(this::toDto);
    }

    public Optional<SeatDto> findSeatById(int seatId)
    {
        log.info("Fetching seat with id: {}", seatId);

        Record seatRecord = dsl.selectFrom(SEATS)
                .where(SEATS.SEAT_ID.eq(seatId))
                .fetchOne();

        return Optional.ofNullable(seatRecord)
                .map(this::toDto);
    }

    public List<SeatDto> findSeatsByVenue(int venueId)
    {
        log.info("Fetching seats with venue id: {}", venueId);

        return dsl.selectFrom(SEATS)
                .where(SEATS.VENUE_ID.eq(venueId))
                .fetch()
                .map(this::toDto);
    }

    public List<SeatDto> findSeatsByVenueAndSeatRow(int venueId, String seatRow)
    {
        log.info("Fetching seats with venue id: {} and seat row: {}", venueId, seatRow);

        return dsl.selectFrom(SEATS)
                .where(SEATS.VENUE_ID.eq(venueId).and(SEATS.SEAT_ROW.eq(seatRow)))
                .fetch()
                .map(this::toDto);
    }

    public int addSeat(SeatDto seatDto)
    {
        log.info("Adding seat");

        return dsl.insertInto(SEATS, SEATS.VENUE_ID, SEATS.SEAT_ROW, SEATS.SEAT_NUMBER)
                .values(seatDto.venueId(), seatDto.seatRow(), seatDto.seatNumber())
                .execute();
    }

    public int modifySeat(SeatDto seatDto)
    {
        log.info("Modifying seat with id: {}", seatDto.seatId());

        return dsl.update(SEATS)
                .set(SEATS.VENUE_ID, seatDto.venueId())
                .set(SEATS.SEAT_ROW, seatDto.seatRow())
                .set(SEATS.SEAT_NUMBER, seatDto.seatNumber())
                .where(SEATS.SEAT_ID.eq(seatDto.seatId()))
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
