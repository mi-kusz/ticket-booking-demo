package org.example.dao;

import org.example.dto.TicketDto;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.example.jooq.generated.tables.Tickets.TICKETS;

public class TicketDao
{
    private static final Logger log = LoggerFactory.getLogger(TicketDao.class);
    private final DSLContext dsl;

    public TicketDao(DSLContext dsl)
    {
        this.dsl = dsl;
    }

    public List<TicketDto> findTickets()
    {
        log.info("Fetching all tickets");

        return dsl.selectFrom(TICKETS)
                .fetch()
                .map(this::toDto);
    }

    public Optional<TicketDto> findTicketById(int ticketId)
    {
        log.info("Fetching ticket with id: {}", ticketId);

        Record ticketRecord = dsl.selectFrom(TICKETS)
                .where(TICKETS.TICKET_ID.eq(ticketId))
                .fetchOne();

        return Optional.ofNullable(ticketRecord)
                .map(this::toDto);
    }

    public List<TicketDto> findTicketsByEventId(int eventId)
    {
        log.info("Fetching tickets with event id: {}", eventId);

        return dsl.selectFrom(TICKETS)
                .where(TICKETS.EVENT_ID.eq(eventId))
                .fetch()
                .map(this::toDto);
    }

    public List<TicketDto> findTicketsByUserId(int userId)
    {
        log.info("Fetching tickets with user id: {}", userId);

        return dsl.selectFrom(TICKETS)
                .where(TICKETS.USER_ID.eq(userId))
                .fetch()
                .map(this::toDto);
    }

    public List<TicketDto> findTicketsByBookedDate(LocalDateTime startTime, LocalDateTime endTime)
    {
        log.info("Fetching tickets with booked date between {} and {}", startTime, endTime);

        return dsl.selectFrom(TICKETS)
                .where(TICKETS.BOOKED_AT.between(startTime, endTime))
                .fetch()
                .map(this::toDto);
    }

    public Optional<TicketDto> addTicket(TicketDto ticketDto)
    {
        log.info("Adding ticket");

        try
        {
            return Optional.ofNullable(dsl.insertInto(TICKETS, TICKETS.EVENT_ID, TICKETS.SEAT_ID, TICKETS.USER_ID, TICKETS.BOOKED_AT)
                    .values(ticketDto.eventId(), ticketDto.seatId(), ticketDto.userId(), ticketDto.bookedAt())
                    .returning()
                    .fetchOne()
            ).map(this::toDto);
        }
        catch (DataAccessException e)
        {
            log.error("Cannot add ticket", e);
            return Optional.empty();
        }
    }

    public Optional<TicketDto> modifyTicket(TicketDto ticketDto)
    {
        log.info("Modifying ticket with id: {}", ticketDto.ticketId());

        try
        {
            return Optional.ofNullable(dsl.update(TICKETS)
                    .set(TICKETS.EVENT_ID, ticketDto.eventId())
                    .set(TICKETS.SEAT_ID, ticketDto.seatId())
                    .set(TICKETS.USER_ID, ticketDto.userId())
                    .set(TICKETS.BOOKED_AT, ticketDto.bookedAt())
                    .where(TICKETS.TICKET_ID.eq(ticketDto.ticketId()))
                    .returning()
                    .fetchOne()
            ).map(this::toDto);
        }
        catch (DataAccessException e)
        {
            log.error("Cannot modify ticket", e);
            return Optional.empty();
        }
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
