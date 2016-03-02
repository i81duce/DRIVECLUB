package com.theironyard;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.*;
//Created by KevinBozic on 3/1/16.

public class MainTest {
    public Connection startConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:./test");
        Main.createTables(conn);
        return conn;
    }

    public void endConnection(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE users");
        stmt.execute("DROP TABLE events");
        conn.close();
    }

    @Test
    public void testUser() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Kevin", "");
        User user = Main.selectUser(conn, "Kevin");
        endConnection(conn);
        assertTrue(user != null);

    }

    @Test
    public void testMessage() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Kevin", "");
        User user = Main.selectUser(conn, "Kevin");// just to make sure id is correct
        Main.insertEvent(conn, user.id, "Ferrari", "1:45:093", "Suzuka Circuit");
        Event event = Main.selectEvent(conn, 1);
        endConnection(conn);
        assertTrue(event != null);
    }
}