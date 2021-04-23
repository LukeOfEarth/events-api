package com.events.eventsusers;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class EventUserKey implements Serializable {

    @Column(name = "Event_Id")
    int eventid;


    @Column(name = "User_Id")
    int userid;


    public EventUserKey() {
    }

    public EventUserKey(int eventid, int userid) {
        this.eventid = eventid;
        this.userid = userid;
    }

    public int getEventid() {
        return eventid;
    }

    public void setEventid(int eventid) {
        this.eventid = eventid;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventUserKey)) return false;
        EventUserKey that = (EventUserKey) o;
        return eventid == that.eventid && userid == that.userid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventid, userid);
    }


}