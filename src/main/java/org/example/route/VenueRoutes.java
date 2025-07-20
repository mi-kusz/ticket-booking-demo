package org.example.route;

import com.google.gson.Gson;
import org.example.dao.VenueDao;
import org.example.dto.VenueDto;
import org.example.util.Util;
import org.jooq.DSLContext;

import java.util.List;
import java.util.Optional;

import static spark.Spark.get;
import static spark.Spark.path;

public class VenueRoutes
{
    private final VenueDao venueDao;
    private final Gson gson;

    public VenueRoutes(DSLContext dsl, Gson gson)
    {
        this.venueDao = new VenueDao(dsl);
        this.gson = gson;
    }

    public void registerRoutes()
    {
        path("/venues", () -> {

        });
    }

    private boolean areParametersValid(String name, String address)
    {
       int parametersCount = Util.countParameters(name, address);

       return parametersCount <= 2;
    }

    private void findVenues()
    {
        get("", ((request, response) -> {
            String name = request.queryParams("name");
            String address = request.queryParams("address");

            if (areParametersValid(name, address))
            {
                List<VenueDto> result;

                if (name != null && address != null)
                {
                    result = venueDao.findVenueByNameAndAddress(name, address);
                }
                else if (name != null)
                {
                    result = venueDao.findVenueByName(name);
                }
                else if (address != null)
                {
                    result = venueDao.findVenueByAddress(address);
                }
                else
                {
                    result = venueDao.findVenues();
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

    private void routeFindVenueById()
    {
        get("/:id", ((request, response) -> {
            int venueId;

            try
            {
                venueId = Integer.parseInt(request.params(":id"));
            }
            catch (NumberFormatException e)
            {
                response.status(400);
                return """
                            Invalid id format. Must be an integer
                            """;
            }

            Optional<VenueDto> result = venueDao.findVenueById(venueId);

            if (result.isPresent())
            {
                response.status(200);
                return gson.toJson(result.get());
            }
            else
            {
                response.status(404);
                return """
                        Venue not found
                        """;
            }
        }));
    }
}
