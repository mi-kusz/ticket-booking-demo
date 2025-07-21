package org.example.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.example.dto.SeatDto;
import org.example.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;

public class SeatDtoAdapter extends TypeAdapter<SeatDto>
{
    @Override
    public void write(JsonWriter jsonWriter, SeatDto seatDto) throws IOException
    {
        jsonWriter.beginObject();
        jsonWriter.name("seatId").value(seatDto.seatId());
        jsonWriter.name("venueId").value(seatDto.venueId());
        jsonWriter.name("seatRow").value(seatDto.seatRow());
        jsonWriter.name("seatNumber").value(seatDto.seatNumber());
        jsonWriter.endObject();
    }

    @Override
    public SeatDto read(JsonReader jsonReader) throws IOException
    {
        int seatId = 0;
        int venueId = 0;
        String seatRow = null;
        int seatNumber = 0;

        jsonReader.beginObject();
        while (jsonReader.hasNext())
        {
            switch (jsonReader.nextName())
            {
                case "seatId" -> seatId = jsonReader.nextInt();
                case "venueId" -> venueId = jsonReader.nextInt();
                case "seatRow" -> seatRow = jsonReader.nextString();
                case "seatNumber" -> seatNumber = jsonReader.nextInt();
            }
        }
        jsonReader.endObject();

        return SeatDto.create(seatId, venueId, seatRow, seatNumber);
    }
}
