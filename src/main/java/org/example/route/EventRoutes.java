package org.example.route;

import com.google.gson.Gson;
import org.example.dao.EventDao;
import org.example.dto.EventDto;
import org.example.util.Util;
import org.jooq.DSLContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import static spark.Spark.get;
import static spark.Spark.path;

public class EventRoutes
{
    private final EventDao eventDao;
    private final Gson gson;

    public EventRoutes(DSLContext dsl, Gson gson)
    {
        this.eventDao = new EventDao(dsl);
        this.gson = gson;
    }

    public void registerRoutes()
    {
        path("/events", () -> {
            routeFindEvents();
            routeFindEventById();
        });
    }

    private boolean areParametersValid(String name, String datetimeStart, String datetimeEnd)
    {
        int parametersCount = Util.countParameters(name, datetimeStart, datetimeEnd);

        boolean nameValid = parametersCount == 1 && name != null;
        boolean datetimeValid = parametersCount == 2 && datetimeStart != null && datetimeEnd != null;
        boolean noParameterValid = parametersCount == 0;

        return nameValid || datetimeValid || noParameterValid;
    }

    private void routeFindEvents()
    {
        get("", (request, response) -> {
            String name = request.queryParams("name");
            String datetimeStart = request.queryParams("datetimeStart");
            String datetimeEnd = request.queryParams("datetimeEnd");

            if (areParametersValid(name, datetimeStart, datetimeEnd))
            {
                List<EventDto> result;

                if (name != null)
                {
                    result = eventDao.findEventsByName(name);
                }
                else if (datetimeStart != null && datetimeEnd != null)
                {
                    try
                    {
                        LocalDateTime start = LocalDateTime.parse(datetimeStart);
                        LocalDateTime end = LocalDateTime.parse(datetimeEnd);

                        result = eventDao.findEventsByDateRange(start, end);
                    }
                    catch (DateTimeParseException e)
                    {
                        response.status(400);
                        return """
                            Invalid datetime format.
                            """;
                    }
                }
                else
                {
                    result = eventDao.findEvents();
                }

                response.status(200);
                return gson.toJson(result);
            }
            else
            {
                response.status(400);
                return """
                        {
                            "error": "Invalid combination of parameters"
                        }
                        """;
            }
        });
    }

    private void routeFindEventById()
    {
        get("/:id", ((request, response) -> {
            int eventId;

            try
            {
                eventId = Integer.parseInt(request.params(":id"));
            }
            catch (NumberFormatException e)
            {
                response.status(400);
                return """
                            Invalid id format. Must be an integer
                            """;
            }

            Optional<EventDto> result = eventDao.findEventById(eventId);

            if (result.isPresent())
            {
                response.status(200);
                return gson.toJson(result.get());
            }
            else
            {
                response.status(404);
                return """
                        Event not found
                        """;
            }
        }));
    }
}
