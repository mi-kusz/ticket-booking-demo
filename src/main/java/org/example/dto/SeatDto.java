package org.example.dto;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class SeatDto
{
    public abstract int seatId();
    public abstract int venueId();
    public abstract String seatRow();
    public abstract int seatNumber();

    public static SeatDto create(int seatId, int venueId, String seatRow, int seatNumber)
    {
        return new AutoValue_SeatDto(seatId, venueId, seatRow, seatNumber);
    }
}
