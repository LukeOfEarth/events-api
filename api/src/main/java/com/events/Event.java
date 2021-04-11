package com.events;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Date;

@Entity
public class Event {
    private int eventId;
    private String name;
    private String description;
    private Date time;
    private String location;
    private int ownerId;

    public Event() {

    }

    public Event(int eventId, String name, String description, Date time, String location, int ownerId) {
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.time = time;
        this.location = location;
        this.ownerId = ownerId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
}
