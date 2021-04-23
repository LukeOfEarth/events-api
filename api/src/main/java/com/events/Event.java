package com.events;

import com.events.eventsusers.EventsUsers;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.sql.Date;
import com.events.Enums.EventStatus;
import java.util.Set;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "eventId")
@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int eventId;
    private String name;
    private String description;
    private Date time;
    private String location;
    private int ownerId;
    private EventStatus status;


    @OneToMany(mappedBy = "event")
    Set<EventsUsers> usersstatus;

    public Event() {

    }

    public Event(int eventId, String name, String description, Date time, String location, int ownerId, int status) {
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.time = time;
        this.location = location;
        this.ownerId = ownerId;
        this.status = EventStatus.values()[status];
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }
  
    public Set<EventsUsers> getUsersstatus() {
        return usersstatus;
    }
}
