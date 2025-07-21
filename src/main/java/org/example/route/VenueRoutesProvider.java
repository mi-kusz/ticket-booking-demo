package org.example.route;

import com.google.gson.Gson;
import org.example.dao.VenueDao;
import org.example.dto.VenueDto;
import org.example.util.Util;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static spark.Spark.get;
import static spark.Spark.path;

public class VenueRoutesProvider implements RoutesProvider
{
    private static final Logger log = LoggerFactory.getLogger(VenueRoutesProvider.class);
    private final VenueDao venueDao;
    private final Gson gson;

    public VenueRoutesProvider(DSLContext dsl, Gson gson)
    {
        this.venueDao = new VenueDao(dsl);
        this.gson = gson;
    }

    @Override
    public void registerRoutes()
    {
        path("/venues", () -> {
            findVenues();
            routeFindVenueById();
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

            log.info("Received GET /venues request with parameters: name={}, address={}", name, address);

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

    private void routeFindVenueById()
    {
        get("/:id", ((request, response) -> {
            String id = request.params(":id");
            log.info("Received GET /venues/{}", id);

            int venueId;

            try
            {
                venueId = Integer.parseInt(id);
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

            Optional<VenueDto> result = venueDao.findVenueById(venueId);

            if (result.isPresent())
            {
                log.info("Venue with id: {} found", id);
                response.status(200);
                return gson.toJson(result.get());
            }
            else
            {
                log.info("Venue with id: {} not found", id);
                response.status(404);
                return """
                        {
                            "error": "Venue not found"
                        }
                        """;
            }
        }));
    }
}
