package org.example.util;

import com.google.gson.Gson;
import org.example.GsonProvider;

import java.util.Map;

public class ErrorMessages
{
    private static final Gson gson;
    public static final String INVALID_ID;
    public static final String INVALID_DATETIME;
    public static final String INVALID_PARAMETERS;
    public static final String JSON_PARSE_ERROR;
    public static final String CANNOT_INSERT_DATA;
    public static final String CANNOT_UPDATE_DATA;

    static
    {
        gson = GsonProvider.getGson();

        INVALID_ID = gson.toJson(error("Invalid id format. Must be an integer"));
        INVALID_DATETIME = gson.toJson(error("Invalid datetime format"));
        INVALID_PARAMETERS = gson.toJson(error("Invalid combination of parameters"));
        JSON_PARSE_ERROR = gson.toJson(error("Cannot parse JSON"));
        CANNOT_INSERT_DATA = gson.toJson(error("Cannot insert provided data"));
        CANNOT_UPDATE_DATA = gson.toJson(error("Cannot update provided data"));
    }

    private static Map<String, String> error(String message)
    {
        return Map.of("error", message);
    }

    public static String notFound(String entity)
    {
        return gson.toJson(error(entity + " not found"));
    }
}
