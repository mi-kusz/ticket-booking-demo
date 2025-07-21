package org.example.route;

import com.google.gson.Gson;
import org.example.dao.EventDao;
import org.example.dto.EventDto;
import org.example.util.Util;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import static spark.Spark.get;
import static spark.Spark.path;

public class EventRoutesProvider implements RoutesProvider
{
    private static final Logger log = LoggerFactory.getLogger(EventRoutesProvider.class);
    private final EventDao eventDao;
    private final Gson gson;

    public EventRoutesProvider(DSLContext dsl, Gson gson)
    {
        this.eventDao = new EventDao(dsl);
        this.gson = gson;
    }

    @Override
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

            log.info("Received GET /events request with parameters: name={}, datetimeStart={}, datetimeEnd={}", name, datetimeStart, datetimeEnd);

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
                        log.error("Cannot parse provided dates: {} and {}", datetimeStart, datetimeEnd, e);

                        response.status(400);
                        return """
                            {
                                "error": "Invalid datetime format"
                            }
                            """;
                    }
                }
                else
                {
                    result = eventDao.findEvents();
                }

                log.info("Responding with {} rows", result.size());
                response.status(200);
                return gson.toJson(result);
            }
            else
            {
                log.error("Wrong combination of parameters");
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
            String id = request.params(":id");
            log.info("Received GET /events/{} request", id);

            int eventId;

            try
            {
                eventId = Integer.parseInt(id);
            }
            catch (NumberFormatException e)
            {
                log.error("Invalid id format: {}", id);
                response.status(400);
                return """
                        {
                            "error": "Invalid id format. Must be an integer"
                        }
                        """;
            }

            Optional<EventDto> result = eventDao.findEventById(eventId);

            if (result.isPresent())
            {
                log.info("Event with id: {} found", id);
                response.status(200);
                return gson.toJson(result.get());
            }
            else
            {
                log.info("Event with id: {} not found", id);
                response.status(404);
                return """
                        {
                            "error": "Event not found"
                        }
                        """;
            }
        }));
    }
}
