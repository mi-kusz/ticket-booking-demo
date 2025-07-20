package org.example.route;

import com.google.gson.Gson;
import org.example.dao.TicketDao;
import org.example.dto.TicketDto;
import org.example.util.Util;
import org.jooq.DSLContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import static spark.Spark.get;
import static spark.Spark.path;

public class TicketRoutes
{
    private final TicketDao ticketDao;
    private final Gson gson;

    public TicketRoutes(DSLContext dsl, Gson gson)
    {
        this.ticketDao = new TicketDao(dsl);
        this.gson = gson;
    }

    public void registerRoutes()
    {
        path("/tickets", () -> {
            routeFindTicket();
            routeFindTicketById();
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
                    response.status(400);
                    return """
                            Invalid id format. Must be an integer
                            """;
                }
                catch (DateTimeParseException e)
                {
                    response.status(400);
                    return """
                            Invalid datetime format.
                            """;
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

    private void routeFindTicketById()
    {
        get("/:id", (request, response) -> {
            int ticketId;

            try
            {
                ticketId = Integer.parseInt(request.params(":id"));
            }
            catch (NumberFormatException e)
            {
                response.status(400);
                return """
                            Invalid id format. Must be an integer
                            """;
            }

            Optional<TicketDto> result = ticketDao.findTicketById(ticketId);

            if (result.isPresent())
            {
                response.status(200);
                return gson.toJson(result.get());
            }
            else
            {
                response.status(404);
                return """
                            Ticket not found
                            """;
            }
        });
    }
}
