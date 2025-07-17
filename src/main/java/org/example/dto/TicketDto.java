package org.example.dto;

import com.google.auto.value.AutoValue;

import java.time.LocalDateTime;

@AutoValue
public abstract class TicketDto
{
    public abstract int ticketId();
    public abstract int eventId();
    public abstract int seatId();
    public abstract int userId();
    public abstract LocalDateTime bookedAt();

    public static TicketDto create(int ticketId, int eventId, int seatId, int userId, LocalDateTime bookedAt)
    {
        return new AutoValue_TicketDto(ticketId, eventId, seatId, userId, bookedAt);
    }
}
