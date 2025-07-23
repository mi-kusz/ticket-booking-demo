package org.example.util;

import org.slf4j.Logger;

public class LogHelper
{
    public static void logListResponse(Logger log, int size)
    {
        log.info("Responding with {} rows", size);
    }

    public static void logWrongParameters(Logger log)
    {
        log.error("Wrong combination of parameters");
    }

    public static void logRequest(Logger log, String method, String path, Object...parameters)
    {
        log.info("Received {} request for {} with parameters: {}", method, path, parameters);
    }

    public static void logInvalidId(Logger log, String id)
    {
        log.error("Invalid id format: {}", id);
    }

    public static void logInvalidDates(Logger log, String startDatetime, String endDateTime)
    {
        log.error("Invalid datetime format: {} and/or {}", startDatetime, endDateTime);
    }

    public static void logWrongJson(Logger log, String dto)
    {
        log.error("Wrong structure of {} JSON", dto);
    }

    public static void logIdFound(Logger log, String entity, String id)
    {
        found(log, entity, "id", id);
    }

    public static void logIdNotFound(Logger log, String entity, String id)
    {
        notFound(log, entity, "id", id);
    }

    public static void logEmailFound(Logger log, String email)
    {
        found(log, "User", "email", email);
    }

    public static void logEmailNotFound(Logger log, String email)
    {
        notFound(log, "User", "email", email);
    }

    public static void logEntityAdded(Logger log, String entity)
    {
        log.info("{} successfully added", entity);
    }

    public static void logEntityNotAdded(Logger log, String entity)
    {
        log.error("{} cannot be added", entity);
    }

    public static void logEntityUpdated(Logger log, String entity)
    {
        log.info("{} successfully updated", entity);
    }

    public static void logEntityNotUpdated(Logger log, String entity)
    {
        log.error("{} cannot be updated", entity);
    }

    private static void found(Logger log, String entity, String field, String value)
    {
        log.info("{} with {}: {} found", entity, field, value);
    }

    private static void notFound(Logger log, String entity, String field, String value)
    {
        log.info("{} with {}: {} not found", entity, field, value);
    }
}
