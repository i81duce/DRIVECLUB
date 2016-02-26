package com.theironyard;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    static HashMap<String , User> users = new HashMap<>();
    static ArrayList<Event> events = new ArrayList<>();

    public static void main(String[] args) {
        Spark.externalStaticFileLocation("public");
        Spark.init();
        Spark.get(
                "/",
                ((request, response) -> {
                    User user = getUserFromSession(request.session());
                    HashMap m = new HashMap();


                    m.put("events", events);
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

                    User user = users.get(userName);
                    if (user == null) {
                        user = new User(userName, userPassword);
                        users.put(user.name, user);
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
                    User user = getUserFromSession(request.session()); //used method to get current user from session
                    String newTrack = request.queryParams("trackName"); //pulled string from the form
                    String newTime = request.queryParams("time"); //pulled string from the form
                    String newCar = request.queryParams("carName"); //pulled string from the form

                    Event newEvent = new Event(user.name, newCar, newTime, newTrack, (events.size() + 1));

                    //newEvent.id = events.size() + 1;

                    events.add(newEvent);

                    response.redirect("/");
                    return "";
                })

        );

        Spark.get(
                "/delete",
                ((request, response) -> {
                    User user = getUserFromSession(request.session());
                    //add checker to make sure user isn't null

                    int index = Integer.valueOf(request.queryParams("id"));
                    Event event = events.get(index -1 );
                    System.out.println(event.onlineId + event.car + event.id);

                    if (user.name.equals(event.onlineId)) {
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
                    User user = getUserFromSession(request.session());

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

    static User getUserFromSession(Session session) {
        String name = session.attribute("userName");
        return users.get(name);
    }
} // end main method