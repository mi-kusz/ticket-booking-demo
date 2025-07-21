package org.example.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.example.dto.TicketDto;
import org.example.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;

public class TicketDtoAdapter extends TypeAdapter<TicketDto>
{
    @Override
    public void write(JsonWriter jsonWriter, TicketDto ticketDto) throws IOException
    {
        jsonWriter.beginObject();
        jsonWriter.name("ticketId").value(ticketDto.ticketId());
        jsonWriter.name("eventId").value(ticketDto.eventId());
        jsonWriter.name("seatId").value(ticketDto.seatId());
        jsonWriter.name("userId").value(ticketDto.userId());
        jsonWriter.name("bookedAt").value(ticketDto.bookedAt().toString());
        jsonWriter.endObject();
    }

    @Override
    public TicketDto read(JsonReader jsonReader) throws IOException
    {
        int ticketId = 0;
        int eventId = 0;
        int seatId = 0;
        int userId = 0;
        LocalDateTime bookedAt = null;

        jsonReader.beginObject();
        while (jsonReader.hasNext())
        {
            switch (jsonReader.nextName())
            {
                case "ticketId" -> ticketId = jsonReader.nextInt();
                case "eventId" -> eventId = jsonReader.nextInt();
                case "seatId" -> seatId = jsonReader.nextInt();
                case "userId" -> userId = jsonReader.nextInt();
                case "bookedAt" -> bookedAt = LocalDateTime.parse(jsonReader.nextString());
            }
        }
        jsonReader.endObject();

        return TicketDto.create(ticketId, eventId, seatId, userId, bookedAt);
    }
}
