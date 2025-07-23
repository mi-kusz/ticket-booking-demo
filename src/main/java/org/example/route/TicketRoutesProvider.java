package org.example.route;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.example.config.GsonProvider;
import org.example.dao.TicketDao;
import org.example.dto.TicketDto;
import org.example.util.ErrorMessages;
import org.example.util.LogHelper;
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
    private final Gson gson = GsonProvider.getGson();

    public TicketRoutesProvider(DSLContext dsl)
    {
        this.ticketDao = new TicketDao(dsl);
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

            LogHelper.logRequest(log, "GET", "/tickets", eventId, userId, datetimeStart, datetimeEnd);

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
                    LogHelper.logInvalidId(log, Objects.requireNonNullElse(eventId, userId));
                    response.status(400);
                    return ErrorMessages.INVALID_ID;
                }
                catch (DateTimeParseException e)
                {
                    LogHelper.logInvalidDates(log, datetimeStart, datetimeEnd);
                    response.status(400);
                    return ErrorMessages.INVALID_DATETIME;
                }

                LogHelper.logListResponse(log, result.size());
                response.status(200);
                return gson.toJson(result);
            }
            else
            {
                LogHelper.logWrongParameters(log);
                response.status(400);
                return ErrorMessages.INVALID_PARAMETERS;
            }
        });
    }

    private void routeFindTicketById()
    {
        get("/:id", (request, response) -> {
            String id = request.params(":id");

            LogHelper.logRequest(log, "GET", "/tickets/id", id);

            int ticketId;

            try
            {
                ticketId = Integer.parseInt(id);
            }
            catch (NumberFormatException e)
            {
                LogHelper.logInvalidId(log, id);
                response.status(400);
                return ErrorMessages.INVALID_ID;
            }

            Optional<TicketDto> result = ticketDao.findTicketById(ticketId);

            if (result.isPresent())
            {
                LogHelper.logIdFound(log, "Ticket", id);
                response.status(200);
                return gson.toJson(result.get());
            }
            else
            {
                LogHelper.logIdNotFound(log, "Ticket", id);
                response.status(404);
                return ErrorMessages.notFound("Ticket");
            }
        });
    }

    private void routeAddTicket()
    {
        post("", (request, response) -> {
            TicketDto ticketDto;

            LogHelper.logRequest(log, "POST", "/tickets");

            try
            {
                ticketDto = gson.fromJson(request.body(), TicketDto.class);
            }
            catch (JsonSyntaxException e)
            {
                LogHelper.logWrongJson(log, "TicketDto");
                response.status(400);
                return ErrorMessages.JSON_PARSE_ERROR;
            }

            Optional<TicketDto> result = ticketDao.addTicket(ticketDto);

            if (result.isPresent())
            {
                LogHelper.logEntityAdded(log, "Ticket");
                response.status(200);
                return gson.toJson(result.get());
            }
            else
            {
                LogHelper.logEntityNotAdded(log, "Ticket");
                response.status(400);
                return ErrorMessages.CANNOT_INSERT_DATA;
            }
        });
    }

    private void routeModifyTicket()
    {
        put("", (request, response) -> {
            TicketDto ticketDto;

            LogHelper.logRequest(log, "PUT", "/tickets");

            try
            {
                ticketDto = gson.fromJson(request.body(), TicketDto.class);
            }
            catch (JsonSyntaxException e)
            {
                LogHelper.logWrongJson(log, "TicketDto");
                response.status(400);
                return ErrorMessages.JSON_PARSE_ERROR;
            }

            Optional<TicketDto> result = ticketDao.modifyTicket(ticketDto);

            if (result.isPresent())
            {
                LogHelper.logEntityUpdated(log, "Ticket");
                response.status(200);
                return gson.toJson(result.get());
            }
            else
            {
                LogHelper.logEntityNotUpdated(log, "Ticket");
                response.status(400);
                return ErrorMessages.CANNOT_UPDATE_DATA;
            }
        });
    }
}
