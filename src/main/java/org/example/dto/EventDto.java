package org.example.dto;

import com.google.auto.value.AutoValue;

import java.time.LocalDateTime;

@AutoValue
public abstract class EventDto
{
    public abstract int eventId();
    public abstract int venueId();
    public abstract String name();
    public abstract LocalDateTime startTime();
    public abstract LocalDateTime endTime();

    public static EventDto create(int eventId, int venueId, String name, LocalDateTime startTime, LocalDateTime endTime)
    {
        return new AutoValue_EventDto(eventId, venueId, name, startTime, endTime);
    }
}
