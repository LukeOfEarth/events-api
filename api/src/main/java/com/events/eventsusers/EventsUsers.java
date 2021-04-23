package com.events.eventsusers;

import javax.persistence.*;

import com.events.Event;
import com.events.users.User;

@Entity
public class EventsUsers {


    @EmbeddedId
    EventsUserKey id;

    @ManyToOne
    @MapsId("eventid")
    @JoinColumn(name = "Event_Id")
    Event event;


    @ManyToOne
    @MapsId("userid")
    @JoinColumn(name = "User_Id")
    User user;

    int Status;

    public EventsUsers(EventsUserKey id, Event event, User user, int status) {
        this.id = id;
        this.event = event;
        this.user = user;
        Status = status;
    }

    public EventsUsers() {

    }

    public EventsUserKey getId() {
        return id;
    }

    public void setId(EventsUserKey id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

}