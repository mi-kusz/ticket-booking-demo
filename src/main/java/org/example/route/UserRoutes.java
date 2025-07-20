package org.example.route;

import com.google.gson.Gson;
import org.example.dao.UserDao;
import org.example.dto.UserDto;
import org.example.util.Util;
import org.jooq.DSLContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import static spark.Spark.get;
import static spark.Spark.path;

public class UserRoutes
{
    private final UserDao userDao;
    private final Gson gson;

    public UserRoutes(DSLContext dsl, Gson gson)
    {
        userDao = new UserDao(dsl);
        this.gson = gson;
    }

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
                        response.status(400);
                        return """
                            Invalid datetime format.
                            """;
                    }
                }
                else
                {
                    result = userDao.findUsers();
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

    private void routeFindUserById()
    {
         get("/id/:id", (request, response) -> {
            int userId;

            try
            {
                userId = Integer.parseInt(request.params(":id"));
            }
            catch (NumberFormatException e)
            {
                response.status(400);
                return """
                            Invalid id format. Must be an integer
                            """;
            }
            Optional<UserDto> result = userDao.findUserById(userId);

            if (result.isPresent())
            {
                response.status(200);
                return gson.toJson(result.get());
            }
            else
            {
                response.status(404);
                return """
                            User not found
                            """;
            }
        });
    }

    private void routeFindUserByEmail()
    {
        get("/email/:email", (request, response) -> {
            String email = request.params(":email");
            Optional<UserDto> result = userDao.findUserByEmail(email);

            if (result.isPresent())
            {
                response.status(200);
                return gson.toJson(result.get());
            }
            else
            {
                response.status(404);
                return """
                            User not found
                            """;
            }
        });
    }
}
