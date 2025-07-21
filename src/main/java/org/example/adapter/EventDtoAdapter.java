package org.example.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.example.dto.EventDto;

import java.io.IOException;
import java.time.LocalDateTime;

public class EventDtoAdapter extends TypeAdapter<EventDto>
{
    @Override
    public void write(JsonWriter jsonWriter, EventDto eventDto) throws IOException
    {
        jsonWriter.beginObject();
        jsonWriter.name("eventId").value(eventDto.eventId());
        jsonWriter.name("venueId").value(eventDto.venueId());
        jsonWriter.name("name").value(eventDto.name());
        jsonWriter.name("startTime").value(eventDto.startTime().toString());
        jsonWriter.name("endTime").value(eventDto.endTime().toString());
        jsonWriter.endObject();
    }

    @Override
    public EventDto read(JsonReader jsonReader) throws IOException
    {
        int eventId = 0;
        int venueId = 0;
        String name = null;
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;

        jsonReader.beginObject();
        while (jsonReader.hasNext())
        {
            switch (jsonReader.nextName())
            {
                case "eventId" -> eventId = jsonReader.nextInt();
                case "venueId" -> venueId = jsonReader.nextInt();
                case "name" -> name = jsonReader.nextString();
                case "startTime" -> startTime = LocalDateTime.parse(jsonReader.nextString());
                case "endTime" -> endTime = LocalDateTime.parse(jsonReader.nextString());
            }
        }
        jsonReader.endObject();

        return EventDto.create(eventId, venueId, name, startTime, endTime);
    }
}
