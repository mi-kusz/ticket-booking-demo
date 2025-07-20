package org.example.route;

import com.google.gson.Gson;
import org.example.dao.SeatDao;
import org.example.dto.SeatDto;
import org.example.util.Util;
import org.jooq.DSLContext;

import java.util.List;
import java.util.Optional;

import static spark.Spark.get;
import static spark.Spark.path;

public class SeatRoutes
{
    private final SeatDao seatDao;
    private final Gson gson;

    public SeatRoutes(DSLContext dsl, Gson gson)
    {
        this.seatDao = new SeatDao(dsl);
        this.gson = gson;
    }

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
        }));
    }

    private void routeFindSeatById()
    {
        get("/:id", ((request, response) -> {
            int seatId;

            try
            {
                seatId = Integer.parseInt(request.params(":id"));
            }
            catch (NumberFormatException e)
            {
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
                response.status(200);
                return gson.toJson(result.get());
            }
            else
            {
                response.status(404);
                return """
                        Seat not found
                        """;
            }
        }));
    }
}
