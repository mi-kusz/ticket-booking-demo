package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import org.example.adapter.LocalDateTimeAdapter;

import java.time.LocalDateTime;

public class GsonProvider
{
    @Getter
    private static final Gson gson;

    static
    {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }
}
