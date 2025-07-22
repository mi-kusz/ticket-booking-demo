package org.example.route;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.example.dao.EventDao;
import org.example.dto.EventDto;
import org.example.util.ErrorMessages;
import org.example.util.Util;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import static spark.Spark.*;

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
            routeAddEvent();
            routeModifyEvent();
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
                        return ErrorMessages.INVALID_DATETIME;
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
                return ErrorMessages.INVALID_PARAMETERS;
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
                return ErrorMessages.INVALID_ID;
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
                return ErrorMessages.notFound("Event");
            }
        }));
    }

    private void routeAddEvent()
    {
        post("", (request, response) -> {
            EventDto eventDto;

            try
            {
                eventDto = gson.fromJson(request.body(), EventDto.class);
            }
            catch (JsonSyntaxException e)
            {
                log.error("Wrong structure of EventDto JSON");
                response.status(400);
                return ErrorMessages.JSON_PARSE_ERROR;
            }

            int affected = eventDao.addEvent(eventDto);

            if (affected == 1)
            {
                log.info("Event added to database");
                response.status(200);
            }
            else
            {
                log.error("Event cannot be added to database");
                response.status(400);
            }

            return gson.toJson(eventDto);
        });
    }

    private void routeModifyEvent()
    {
        put("", (request, response) -> {
            EventDto eventDto;

            try
            {
                eventDto = gson.fromJson(request.body(), EventDto.class);
            }
            catch (JsonSyntaxException e)
            {
                log.error("Wrong structure of EventDto JSON");
                response.status(400);
                return ErrorMessages.JSON_PARSE_ERROR;
            }

            int affected = eventDao.modifyEvent(eventDto);

            if (affected == 1)
            {
                log.info("Event modified");
                response.status(200);
            }
            else
            {
                log.error("Event cannot be modified");
                response.status(400);
            }

            return gson.toJson(eventDto);
        });
    }
}
