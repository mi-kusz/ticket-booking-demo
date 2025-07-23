package org.example.route;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.example.dao.SeatDao;
import org.example.dto.SeatDto;
import org.example.util.ErrorMessages;
import org.example.util.LogHelper;
import org.example.util.Util;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static spark.Spark.*;

public class SeatRoutesProvider implements RoutesProvider
{
    private static final Logger log = LoggerFactory.getLogger(SeatRoutesProvider.class);
    private final SeatDao seatDao;
    private final Gson gson;

    public SeatRoutesProvider(DSLContext dsl, Gson gson)
    {
        this.seatDao = new SeatDao(dsl);
        this.gson = gson;
    }

    @Override
    public void registerRoutes()
    {
        path("/seats", () -> {
            routeFindSeats();
            routeFindSeatById();
            routeAddSeat();
            routeModifySeat();
        });
    }

    private boolean areParametersValid(String venueId, String seatRow)
    {
        int parametersCount = Util.countParameters(venueId, seatRow);

        boolean venueIdAndSeatRowValid = parametersCount == 2;
        boolean venueIdValid = parametersCount == 1 && venueId != null;
        boolean noParameterValid = parametersCount == 0;

        return venueIdAndSeatRowValid || venueIdValid || noParameterValid;
    }

    private void routeFindSeats()
    {
        get("", ((request, response) -> {
            String venueId = request.queryParams("venueId");
            String seatRow = request.queryParams("seatRow");

            LogHelper.logRequest(log, "GET", "/seats", venueId, seatRow);

            if (areParametersValid(venueId, seatRow))
            {
                List<SeatDto> result;

                if (venueId != null)
                {
                    int venue;

                    try
                    {
                        venue = Integer.parseInt(venueId);
                    }
                    catch (NumberFormatException e)
                    {
                        LogHelper.logInvalidId(log, venueId);
                        response.status(400);
                        return ErrorMessages.INVALID_ID;
                    }

                    if (seatRow != null)
                    {
                        result = seatDao.findSeatsByVenueAndSeatRow(venue, seatRow);
                    }
                    else
                    {
                        result = seatDao.findSeatsByVenue(venue);
                    }
                }
                else
                {
                    result = seatDao.findSeats();
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
        }));
    }

    private void routeFindSeatById()
    {
        get("/:id", ((request, response) -> {
            String id = request.params(":id");

            LogHelper.logRequest(log, "GET", "/seats/id", id);

            int seatId;

            try
            {
                seatId = Integer.parseInt(id);
            }
            catch (NumberFormatException e)
            {
                LogHelper.logInvalidId(log, id);
                response.status(400);
                return ErrorMessages.INVALID_ID;
            }

            Optional<SeatDto> result = seatDao.findSeatById(seatId);

            if (result.isPresent())
            {
                LogHelper.logIdFound(log, "Seat", id);
                response.status(200);
                return gson.toJson(result.get());
            }
            else
            {
                LogHelper.logIdNotFound(log, "Seat", id);
                response.status(404);
                return ErrorMessages.notFound("Seat");
            }
        }));
    }

    private void routeAddSeat()
    {
        post("", (request, response) ->  {
            SeatDto seatDto;

            LogHelper.logRequest(log, "POST", "/seats");

            try
            {
                seatDto = gson.fromJson(request.body(), SeatDto.class);
            }
            catch (JsonSyntaxException e)
            {
                LogHelper.logWrongJson(log, "SeatDto");
                response.status(400);
                return ErrorMessages.JSON_PARSE_ERROR;
            }

            int affected = seatDao.addSeat(seatDto);

            if (affected == 1)
            {
                LogHelper.logEntityAdded(log, "Seat");
                response.status(200);
            }
            else
            {
                LogHelper.logEntityNotAdded(log, "Seat");
                log.error("Seat cannot be added to database");
                response.status(400);
            }

            return gson.toJson(seatDto);
        });
    }

    private void routeModifySeat()
    {
        put("", (request, response) ->  {
            SeatDto seatDto;

            LogHelper.logRequest(log, "PUT", "/seats");

            try
            {
                seatDto = gson.fromJson(request.body(), SeatDto.class);
            }
            catch (JsonSyntaxException e)
            {
                LogHelper.logWrongJson(log, "SeatDto");
                response.status(400);
                return ErrorMessages.JSON_PARSE_ERROR;
            }

            int affected = seatDao.modifySeat(seatDto);

            if (affected == 1)
            {
                LogHelper.logEntityUpdated(log, "Seat");
                response.status(200);
            }
            else
            {
                LogHelper.logEntityNotUpdated(log, "Seat");
                response.status(400);
            }

            return gson.toJson(seatDto);
        });
    }
}
