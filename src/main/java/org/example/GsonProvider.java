package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import org.example.adapter.*;
import org.example.dto.*;

import java.time.LocalDateTime;

public class GsonProvider
{
    @Getter
    private static final Gson gson;

    static
    {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(UserDto.class, new UserDtoAdapter())
                .registerTypeAdapter(SeatDto.class, new SeatDtoAdapter())
                .registerTypeAdapter(EventDto.class, new EventDtoAdapter())
                .registerTypeAdapter(VenueDto.class, new VenueDtoAdapter())
                .registerTypeAdapter(TicketDto.class, new TicketDtoAdapter())
                .create();
    }
}
