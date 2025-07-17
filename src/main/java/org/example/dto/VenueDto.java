package org.example.dto;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class VenueDto
{
    public abstract int venueId();
    public abstract String name();
    public abstract String address();

    public static VenueDto create(int venueId, String name, String address)
    {
        return new AutoValue_VenueDto(venueId, name, address);
    }
}
