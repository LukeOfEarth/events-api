package com.events.users;


import com.events.Enums.UserAccountStatus;
import com.events.eventsusers.EventUser;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.util.Set;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "userId")

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;
    private String name;
    private String email;
    private UserAccountStatus status;

    @JsonIgnore
    private String authId;


    @OneToMany(mappedBy = "user")
    Set<EventUser> eventsstatus;

    public User() {

    }

    public User(String name, String email, int status) {
        this.name = name;
        this.email = email;
        this.status = UserAccountStatus.values()[status];
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setStatus(UserAccountStatus status){
        this.status = status;
    }

    public UserAccountStatus getStatus(){
        return status;
    }

    public Set<EventUser> getEventsstatus() {
        return eventsstatus;
    }

    public String getAuthId() { return authId; }

    public void setAuthId(String authId) { this.authId = authId; }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
