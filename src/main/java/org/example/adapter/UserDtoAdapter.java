package org.example.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.example.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;

public class UserDtoAdapter extends TypeAdapter<UserDto>
{
    @Override
    public void write(JsonWriter jsonWriter, UserDto userDto) throws IOException
    {
        jsonWriter.beginObject();
        jsonWriter.name("userId").value(userDto.userId());
        jsonWriter.name("name").value(userDto.name());
        jsonWriter.name("email").value(userDto.email());
        jsonWriter.name("createdAt").value(userDto.createdAt().toString());
        jsonWriter.endObject();
    }

    @Override
    public UserDto read(JsonReader jsonReader) throws IOException
    {
        int userId = 0;
        String name = null;
        String email = null;
        LocalDateTime createdAt = null;

        jsonReader.beginObject();
        while (jsonReader.hasNext())
        {
            switch (jsonReader.nextName())
            {
                case "userId" -> userId = jsonReader.nextInt();
                case "name" -> name = jsonReader.nextString();
                case "email" -> email = jsonReader.nextString();
                case "createdAt" -> createdAt = LocalDateTime.parse(jsonReader.nextString());
            }
        }
        jsonReader.endObject();

        return UserDto.create(userId, name, email, createdAt);
    }
}
