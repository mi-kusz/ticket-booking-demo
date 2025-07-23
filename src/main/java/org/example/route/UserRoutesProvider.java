package org.example.route;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.example.dao.UserDao;
import org.example.dto.UserDto;
import org.example.util.ErrorMessages;
import org.example.util.LogHelper;
import org.example.util.Util;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import static spark.Spark.*;

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
            routeAddUser();
            routeModifyUser();
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

            LogHelper.logRequest(log, "GET", "/users", name, datetimeStart, datetimeEnd);

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
                        LogHelper.logInvalidDates(log, datetimeStart, datetimeEnd);
                        response.status(400);
                        return ErrorMessages.INVALID_DATETIME;
                    }
                }
                else
                {
                    result = userDao.findUsers();
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

    private void routeFindUserById()
    {
         get("/id/:id", (request, response) -> {
             String id = request.params(":id");

             LogHelper.logRequest(log, "GET", "/users/id", id);

             int userId;

            try
            {
                userId = Integer.parseInt(id);
            }
            catch (NumberFormatException e)
            {
                LogHelper.logInvalidId(log, id);
                response.status(400);
                return ErrorMessages.INVALID_ID;
            }
            Optional<UserDto> result = userDao.findUserById(userId);

            if (result.isPresent())
            {
                LogHelper.logIdFound(log, "User", id);
                response.status(200);
                return gson.toJson(result.get());
            }
            else
            {
                LogHelper.logIdNotFound(log, "User", id);
                response.status(404);
                return ErrorMessages.notFound("User");
            }
        });
    }

    private void routeFindUserByEmail()
    {
        get("/email/:email", (request, response) -> {
            String email = request.params(":email");

            LogHelper.logRequest(log, "GET", "/users/email", email);

            Optional<UserDto> result = userDao.findUserByEmail(email);

            if (result.isPresent())
            {
                LogHelper.logEmailFound(log, email);
                response.status(200);
                return gson.toJson(result.get());
            }
            else
            {
                LogHelper.logEmailNotFound(log, email);
                response.status(404);
                return ErrorMessages.notFound("User");
            }
        });
    }

    private void routeAddUser()
    {
        post("", (request, response) -> {
            UserDto userDto;

            LogHelper.logRequest(log, "POST", "/users");

            try
            {
                userDto = gson.fromJson(request.body(), UserDto.class);
            }
            catch (JsonSyntaxException e)
            {
                LogHelper.logWrongJson(log, "UserDto");
                response.status(400);
                return ErrorMessages.JSON_PARSE_ERROR;
            }

            Optional<UserDto> result = userDao.addUser(userDto);

            if (result.isPresent())
            {
                LogHelper.logEntityAdded(log, "User");
                response.status(200);
                return gson.toJson(result.get());
            }
            else
            {
                LogHelper.logEntityNotAdded(log, "User");
                response.status(400);
                return ErrorMessages.CANNOT_INSERT_DATA;
            }
        });
    }


    private void routeModifyUser()
    {
        put("", (request, response) -> {
            UserDto userDto;

            LogHelper.logRequest(log, "PUT", "/users");

            try
            {
                userDto = gson.fromJson(request.body(), UserDto.class);
            }
            catch (JsonSyntaxException e)
            {
                LogHelper.logWrongJson(log, "UserDto");
                response.status(400);
                return ErrorMessages.JSON_PARSE_ERROR;
            }

            Optional<UserDto> result = userDao.modifyUser(userDto);

            if (result.isPresent())
            {
                LogHelper.logEntityUpdated(log, "User");
                response.status(200);
                return gson.toJson(result.get());
            }
            else
            {
                LogHelper.logEntityNotUpdated(log, "User");
                response.status(400);
                return ErrorMessages.CANNOT_UPDATE_DATA;
            }
        });
    }
}
