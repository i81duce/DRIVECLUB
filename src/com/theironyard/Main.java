package com.theironyard;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, name VARCHAR, password VARCHAR)");
        stmt.execute("CREATE TABLE IF NOT EXISTS events (id IDENTITY, user_id INT, car VARCHAR, time VARCHAR, track_name VARCHAR)");
    }

    public static void insertUser(Connection conn, String name, String password) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES (NULL, ?, ?)");
        stmt.setString(1, name);
        stmt.setString(2, password);
        stmt.execute();
    }

    public static User selectUser(Connection conn, String name) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE name = ?");
        stmt.setString(1, name);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            int id = results.getInt("id");
            String password = results.getString("password");
            return new User(id, name, password);
        }
        return null;
    }

    public static void insertEvent(Connection conn, int userId, String car, String time, String trackName) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO events VALUES (NULL, ?, ?, ?, ?)");
        stmt.setInt(1, userId);
        stmt.setString(2, car);
        stmt.setString(3, time);
        stmt.setString(4, trackName);
        stmt.execute();
    }

    public static Event selectEvent(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM events INNER JOIN users ON events.user_id = users.id WHERE events.id = ?");
        stmt.setInt(1, id);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            String userName = results.getString("users.name");
            String car = results.getString("events.car");
            String time = results.getString("events.time");
            String trackName = results.getString("events.track_name");

            return new Event(id, userName, car, time, trackName);
        }
        return null;
    }

    public static void deleteEvent() {
        //////////////////////////////////////////////////////
    }

    public static void updateField() {
        //////////////////////////////////////////////////////
    }

    public static ArrayList<Event> displayEvents(Connection conn) throws SQLException {
        ArrayList<Event> events = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM events INNER JOIN users ON events.user_id = users.id");
        ResultSet results = stmt.executeQuery();
        while (results.next()) {
            int id = results.getInt("events.id");
            String name = results.getString("users.name");
            String car = results.getString("events.car");
            String time = results.getString("events.time");
            String trackName = results.getString("events.track_name");
            Event event = new Event(id, name, car, time, trackName);
            events.add(event);
        }
        return events;
    }

    static HashMap<String , User> users = new HashMap<>();
    static ArrayList<Event> events = new ArrayList<>();

    public static void main(String[] args) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);
        //Spark.externalStaticFileLocation("public"); // use if you wanna look for css file locally
        Spark.staticFileLocation("public"); // use if you wanna put the css file in a jar file
        Spark.init();
        Spark.get(
                "/",
                ((request, response) -> {
                    User user = getUserFromSession(request.session(), conn);
                    HashMap m = new HashMap();


                    m.put("events", displayEvents(conn));

                    if (user == null) {
                        return new ModelAndView(m, "login.html");
                    } else {
                        m.put("butts", user);
                        return new ModelAndView(m, "home.html");
                    }
                }),
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/login",
                ((request, response) -> {
                    String userName = request.queryParams("loginName");
                    String userPassword = request.queryParams("loginPassword");
                    if (userName == null) {
                        throw new Exception("Login name not found.");
                    }

                    User user = selectUser(conn, userName);
                    if (user == null) {
                        insertUser(conn, userName, userPassword);
//                        user = new User(userName, userPassword);
//                        users.put(user.name, user);
                    }

                    Session session = request.session();
                    session.attribute("userName", userName);

                    response.redirect("/");
                    return "";
                })
        );

        Spark.post(
                "/logout",
                ((request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return "/";
                })
        );

        Spark.post(
                "/create-event",
                ((request, response) -> {
                    User user = getUserFromSession(request.session(), conn); //used method to get current user from session


                    String newTrack = request.queryParams("trackName"); //pulled string from the form
                    String newTime = request.queryParams("time"); //pulled string from the form
                    String newCar = request.queryParams("carName"); //pulled string from the form

                    //Event newEvent = new Event(user.id, newCar, newTime, newTrack, (events.size() + 1));

//                    newEvent.id = events.size() + 1;

                    //events.add(newEvent);
                    insertEvent(conn, user.id, newCar, newTrack, newTime);

                    response.redirect("/");
                    return "";
                })

        );

        Spark.get(
                "/delete",
                ((request, response) -> {
                    User user = getUserFromSession(request.session(), conn);
                    //add checker to make sure user isn't null

                    int index = Integer.valueOf(request.queryParams("id"));
                    Event event = events.get(index -1 );
                    System.out.println(event.userName + event.car + event.id);

                    if (user.name.equals(event.userName)) {
                        events.remove(event);
                    } else {
                        response.redirect("/");
                    }
                    response.redirect("/");
                    return "";
                })
        );

        Spark.get(
                "/edit-event",
                ((request, response) -> {
                    User user = getUserFromSession(request.session(), conn);

                    HashMap m = new HashMap();
                    int eventId = Integer.valueOf(request.queryParams("eventId"));
                    Event editEvent = events.get(eventId-1);
                    m.put ("editEvent", editEvent);

                    return new ModelAndView(m, "edit.html");

                }),
                new MustacheTemplateEngine()
        );

        Spark.post(
                "edit-event",
                ((request, response) -> {
                    int eventId = Integer.valueOf(request.queryParams("id"));
                    Event editEvent = events.get(eventId-1);


                    User user = users.get(users);
                    if (user == null) {
                        String newTrack = request.queryParams("trackName");// requesting params of new track
                        String newTime = request.queryParams("time");
                        String newCar = request.queryParams("carName");
                        editEvent.trackName = newTrack; // setting trackname to newTrack
                        editEvent.time = newTime;
                        editEvent.car = newCar;
                    }

                    response.redirect("/");
                    return "";
                })
        );
    }

    static User getUserFromSession(Session session, Connection conn) throws SQLException {
        String name = session.attribute("userName");
        User user = selectUser(conn, name);
        return user;
    }
} // end main method