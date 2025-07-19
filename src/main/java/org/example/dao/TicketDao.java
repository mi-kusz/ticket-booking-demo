package org.example.dao;

import org.example.dto.TicketDto;
import org.jooq.DSLContext;
import org.jooq.Record;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.example.jooq.generated.tables.Tickets.TICKETS;

public class TicketDao
{
    private final DSLContext dsl;

    public TicketDao(DSLContext dsl)
    {
        this.dsl = dsl;
    }

    public Optional<TicketDto> findTicketById(int ticketId)
    {
        Record ticketRecord = dsl.selectFrom(TICKETS)
                .where(TICKETS.TICKET_ID.eq(ticketId))
                .fetchOne();

        return Optional.ofNullable(ticketRecord)
                .map(this::toDto);
    }

    public List<TicketDto> findTicketsByEventId(int eventId)
    {
        return dsl.selectFrom(TICKETS)
                .where(TICKETS.EVENT_ID.eq(eventId))
                .fetch()
                .map(this::toDto);
    }

    public List<TicketDto> findTicketsByUserId(int userId)
    {
        return dsl.selectFrom(TICKETS)
                .where(TICKETS.USER_ID.eq(userId))
                .fetch()
                .map(this::toDto);
    }

    public List<TicketDto> findTicketsByBookedDate(LocalDateTime startTime, LocalDateTime endTime)
    {
        return dsl.selectFrom(TICKETS)
                .where(TICKETS.BOOKED_AT.between(startTime, endTime))
                .fetch()
                .map(this::toDto);
    }

    public int addTicket(TicketDto ticketDto)
    {
        return dsl.insertInto(TICKETS, TICKETS.EVENT_ID, TICKETS.SEAT_ID, TICKETS.USER_ID, TICKETS.BOOKED_AT)
                .values(ticketDto.eventId(), ticketDto.seatId(), ticketDto.userId(), ticketDto.bookedAt())
                .execute();
    }

    public int modifyTicket(TicketDto ticketDto)
    {
        return dsl.update(TICKETS)
                .set(TICKETS.EVENT_ID, ticketDto.eventId())
                .set(TICKETS.SEAT_ID, ticketDto.seatId())
                .set(TICKETS.USER_ID, ticketDto.userId())
                .set(TICKETS.BOOKED_AT, ticketDto.bookedAt())
                .where(TICKETS.TICKET_ID.eq(ticketDto.ticketId()))
                .execute();
    }

    private TicketDto toDto(Record r)
    {
        return TicketDto.create(
                r.get(TICKETS.TICKET_ID),
                r.get(TICKETS.EVENT_ID),
                r.get(TICKETS.SEAT_ID),
                r.get(TICKETS.USER_ID),
                r.get(TICKETS.BOOKED_AT)
        );
    }
}
