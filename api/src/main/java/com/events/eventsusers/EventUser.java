package com.events.eventsusers;

import javax.persistence.*;

import com.events.Enums.UserStatus;
import com.events.events.Event;
import com.events.users.User;

@Entity
public class EventUser {


    @EmbeddedId
    EventUserKey id;

    @ManyToOne
    @MapsId("eventid")
    @JoinColumn(name = "Event_Id")
    Event event;


    @ManyToOne
    @MapsId("userid")
    @JoinColumn(name = "User_Id")
    User user;

    UserStatus status;

    public EventUser(EventUserKey id, Event event, User user, UserStatus status) {
        this.id = id;
        this.event = event;
        this.user = user;
        this.status = status;
    }

    public EventUser() {

    }

    public EventUserKey getId() {
        return id;
    }

    public void setId(EventUserKey id) {
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

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }


}