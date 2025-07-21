package org.example.route;

import com.google.gson.Gson;
import org.example.dao.SeatDao;
import org.example.dto.SeatDto;
import org.example.util.Util;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static spark.Spark.get;
import static spark.Spark.path;

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

            log.info("Received GET /seats request with parameters: venueId={}, seatRow={}", venueId, seatRow);

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
                        log.error("Invalid id format: {}", venueId);
                        response.status(400);
                        return """
                        {
                            "error": "Invalid id format. Must be an integer"
                        }
                        """;
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
        }));
    }

    private void routeFindSeatById()
    {
        get("/:id", ((request, response) -> {
            String id = request.params(":id");
            log.info("Received GET /seats/{} request", id);

            int seatId;

            try
            {
                seatId = Integer.parseInt(id);
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

            Optional<SeatDto> result = seatDao.findSeatById(seatId);

            if (result.isPresent())
            {
                log.info("Seat with id: {} found", id);
                response.status(200);
                return gson.toJson(result.get());
            }
            else
            {
                log.info("Seat with id: {} not found", id);
                response.status(404);
                return """
                        {
                            "error": "Seat not found"
                        }
                        """;
            }
        }));
    }
}
