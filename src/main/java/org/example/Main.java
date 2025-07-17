package org.example;

import org.example.config.Database;
import org.jooq.DSLContext;

import java.sql.SQLException;

public class Main
{
    public static void main(String[] args) throws SQLException
    {
        DSLContext dsl = Database.getDslContext();
    }
}