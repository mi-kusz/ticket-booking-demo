package org.example;

import org.example.config.Database;
import org.jooq.DSLContext;

import java.sql.SQLException;

public class Main
{
    public static void main(String[] args) throws SQLException
    {
        // TODO: Change AutoValue to Lombok?
        // TODO: Add tests for modifying values
        // TODO: Add REST endpoints

        DSLContext dsl = Database.getDslContext();
    }
}