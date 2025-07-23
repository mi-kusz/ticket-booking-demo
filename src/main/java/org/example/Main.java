package org.example;

import org.example.config.Database;
import org.example.route.*;
import org.jooq.DSLContext;

import java.sql.SQLException;

import static spark.Spark.before;
import static spark.Spark.port;

public class Main
{
    public static void main(String[] args) throws SQLException
    {
        try
        {
            port(8080);
            before((req, res) -> res.type("application/json"));

            DSLContext dsl = Database.getDslContext();

            new UserRoutesProvider(dsl).registerRoutes();
            new SeatRoutesProvider(dsl).registerRoutes();
            new VenueRoutesProvider(dsl).registerRoutes();
            new EventRoutesProvider(dsl).registerRoutes();
            new TicketRoutesProvider(dsl).registerRoutes();
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
    }
}