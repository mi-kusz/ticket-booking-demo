package org.example.route;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.example.dao.TicketDao;
import org.example.dto.TicketDto;
import org.example.util.Util;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static spark.Spark.*;

public class TicketRoutesProvider implements RoutesProvider
{
    private static final Logger log = LoggerFactory.getLogger(TicketRoutesProvider.class);
    private final TicketDao ticketDao;
    private final Gson gson;

    public TicketRoutesProvider(DSLContext dsl, Gson gson)
    {
        this.ticketDao = new TicketDao(dsl);
        this.gson = gson;
    }

    @Override
    public void registerRoutes()
    {
        path("/tickets", () -> {
            routeFindTicket();
            routeFindTicketById();
            routeAddTicket();
            routeModifyTicket();
        });
    }

    private boolean areParametersValid(String eventId, String userId, String datetimeStart, String datetimeEnd)
    {
        int parametersCount = Util.countParameters(eventId, userId, datetimeStart, datetimeEnd);

        boolean eventValid = parametersCount == 1 && eventId != null;
        boolean userValid = parametersCount == 1 && userId != null;
        boolean dateValid = parametersCount == 2 && datetimeStart != null && datetimeEnd != null;
        boolean noParameterValid = parametersCount == 0;

        return eventValid || userValid || dateValid || noParameterValid;
    }

    private void routeFindTicket()
    {
        get("", (request, response) -> {
            String eventId = request.queryParams("eventId");
            String userId = request.queryParams("userId");
            String datetimeStart = request.queryParams("datetimeStart");
            String datetimeEnd = request.queryParams("datetimeEnd");

            log.info("Received GET /tickets request with parameters: eventId={}, userId={}, datetimeStart={}, datetimeEnd={}", eventId, userId, datetimeStart, datetimeEnd);

            if (areParametersValid(eventId, userId, datetimeStart, datetimeEnd))
            {
                List<TicketDto> result;

                try
                {
                    if (eventId != null)
                    {
                        int event = Integer.parseInt(eventId);
                        result = ticketDao.findTicketsByEventId(event);
                    }
                    else if (userId != null)
                    {
                        int user = Integer.parseInt(userId);
                        result = ticketDao.findTicketsByUserId(user);
                    }
                    else if (datetimeStart != null && datetimeEnd != null)
                    {
                        LocalDateTime start = LocalDateTime.parse(datetimeStart);
                        LocalDateTime end = LocalDateTime.parse(datetimeEnd);
                        result = ticketDao.findTicketsByBookedDate(start, end);
                    }
                    else
                    {
                        result = ticketDao.findTickets();
                    }
                }
                catch (NumberFormatException e)
                {
                    log.error("Invalid id format: {}", Objects.requireNonNullElse(eventId, userId));
                    response.status(400);
                    return """
                        {
                            "error": "Invalid id format. Must be an integer"
                        }
                        """;
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

    private void routeFindTicketById()
    {
        get("/:id", (request, response) -> {
            String id = request.params(":id");
            log.info("Received GET /tickets/{}", id);

            int ticketId;

            try
            {
                ticketId = Integer.parseInt(id);
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

            Optional<TicketDto> result = ticketDao.findTicketById(ticketId);

            if (result.isPresent())
            {
                log.info("Ticket with id: {} found", id);
                response.status(200);
                return gson.toJson(result.get());
            }
            else
            {
                log.info("Ticket with id: {} not found", id);
                response.status(404);
                return """
                        {
                            "error": "Ticket not found"
                        }
                        """;
            }
        });
    }

    private void routeAddTicket()
    {
        post("", (request, response) -> {
            TicketDto ticketDto;

            try
            {
                ticketDto = gson.fromJson(request.body(), TicketDto.class);
            }
            catch (JsonSyntaxException e)
            {
                log.error("Wrong structure of TicketDto JSON");
                response.status(400);
                return """
                            "error": "Cannot parse JSON"
                        """;
            }

            int affected = ticketDao.addTicket(ticketDto);

            if (affected == 1)
            {
                log.info("Ticket added to database");
                response.status(200);
            }
            else
            {
                log.error("Ticket cannot be added to database");
                response.status(400);
            }

            return gson.toJson(ticketDto);
        });
    }

    private void routeModifyTicket()
    {
        put("", (request, response) -> {
            TicketDto ticketDto;

            try
            {
                ticketDto = gson.fromJson(request.body(), TicketDto.class);
            }
            catch (JsonSyntaxException e)
            {
                log.error("Wrong structure of TicketDto JSON");
                response.status(400);
                return """
                            "error": "Cannot parse JSON"
                        """;
            }

            int affected = ticketDao.modifyTicket(ticketDto);

            if (affected == 1)
            {
                log.info("Ticket modified");
                response.status(200);
            }
            else
            {
                log.error("Ticket cannot be modified");
                response.status(400);
            }

            return gson.toJson(ticketDto);
        });
    }
}
