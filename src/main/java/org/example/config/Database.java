package org.example.config;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database
{
    private static DSLContext dsl;

    public static DSLContext getDslContext() throws SQLException
    {
        if (dsl == null)
        {
            String url = Config.get("db.url");
            String user = Config.get("db.user");
            String password = Config.get("db.password");
            Connection conn = DriverManager.getConnection(url, user, password);
            dsl = DSL.using(conn, SQLDialect.POSTGRES);
        }

        return dsl;
    }
}
