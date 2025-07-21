package org.example.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.example.dto.UserDto;
import org.example.dto.VenueDto;

import java.io.IOException;
import java.time.LocalDateTime;

public class VenueDtoAdapter extends TypeAdapter<VenueDto>
{
    @Override
    public void write(JsonWriter jsonWriter, VenueDto venueDto) throws IOException
    {
        jsonWriter.beginObject();
        jsonWriter.name("venueId").value(venueDto.venueId());
        jsonWriter.name("name").value(venueDto.name());
        jsonWriter.name("address").value(venueDto.address());
        jsonWriter.endObject();
    }

    @Override
    public VenueDto read(JsonReader jsonReader) throws IOException
    {
        int venueId = 0;
        String name = null;
        String address = null;

        jsonReader.beginObject();
        while (jsonReader.hasNext())
        {
            switch (jsonReader.nextName())
            {
                case "venueId" -> venueId = jsonReader.nextInt();
                case "name" -> name = jsonReader.nextString();
                case "address" -> address = jsonReader.nextString();
            }
        }
        jsonReader.endObject();

        return VenueDto.create(venueId, name, address);
    }
}
