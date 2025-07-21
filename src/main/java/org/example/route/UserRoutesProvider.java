package org.example.route;

import com.google.gson.Gson;
import org.example.dao.UserDao;
import org.example.dto.UserDto;
import org.example.util.Util;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import static spark.Spark.get;
import static spark.Spark.path;

public class UserRoutesProvider implements RoutesProvider
{
    private static final Logger log = LoggerFactory.getLogger(UserRoutesProvider.class);
    private final UserDao userDao;
    private final Gson gson;

    public UserRoutesProvider(DSLContext dsl, Gson gson)
    {
        userDao = new UserDao(dsl);
        this.gson = gson;
    }

    @Override
    public void registerRoutes()
    {
        path("/users", () -> {
            routeFindUsers();
            routeFindUserById();
            routeFindUserByEmail();
        });
    }

    private boolean areParametersValid(String name, String datetimeStart, String datetimeEnd)
    {
        int parametersCount = Util.countParameters(name, datetimeStart, datetimeEnd);

        boolean dateTimeValid = parametersCount == 2 && datetimeStart != null && datetimeEnd != null;
        boolean nameValid = parametersCount == 1 && name != null;
        boolean noParameterValid = parametersCount == 0;

        return dateTimeValid || nameValid || noParameterValid;
    }

    private void routeFindUsers()
    {
        get("", (request, response) -> {
            String name = request.queryParams("name");
            String datetimeStart = request.queryParams("datetimeStart");
            String datetimeEnd = request.queryParams("datetimeEnd");

            log.info("Received GET /users request with parameters: name={}, datetimeStart={}, datetimeEnd={}", name, datetimeStart, datetimeEnd);

            if (areParametersValid(name, datetimeStart, datetimeEnd))
            {
                List<UserDto> result;

                if (name != null)
                {
                    result = userDao.findUsersByName(name);
                }
                else if (datetimeStart != null && datetimeEnd != null)
                {
                    try
                    {
                        LocalDateTime start = LocalDateTime.parse(datetimeStart);
                        LocalDateTime end = LocalDateTime.parse(datetimeEnd);

                        result = userDao.findUserByCreationDatetime(start, end);
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
                }
                else
                {
                    result = userDao.findUsers();
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

    private void routeFindUserById()
    {
         get("/id/:id", (request, response) -> {
             String id = request.params(":id");
             log.info("Received GET /users/id/{} request", id);

             int userId;

            try
            {
                userId = Integer.parseInt(id);
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
            Optional<UserDto> result = userDao.findUserById(userId);

            if (result.isPresent())
            {
                log.info("User with id: {} found", id);
                response.status(200);
                return gson.toJson(result.get());
            }
            else
            {
                log.info("User with id: {} not found", id);
                response.status(404);
                return """
                        {
                            "error": "User not found"
                        }
                        """;
            }
        });
    }

    private void routeFindUserByEmail()
    {
        get("/email/:email", (request, response) -> {
            String email = request.params(":email");
            log.info("Received GET /users/email/{} request", email);

            Optional<UserDto> result = userDao.findUserByEmail(email);

            if (result.isPresent())
            {
                log.info("User with email: {} found", email);
                response.status(200);
                return gson.toJson(result.get());
            }
            else
            {
                log.info("User with email: {} not found", email);
                response.status(404);
                return """
                        {
                            "error": "User not found"
                        }
                        """;
            }
        });
    }
}
