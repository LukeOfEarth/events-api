package com.events.eventsusers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class EventUserService {
    @Autowired
    private EventsUsersRepository repo;



    public EventUser save(EventUser eventuser) {
        return repo.save(eventuser);
    }

    public EventUser get(EventUserKey id) {
        Optional<EventUser> eventuser = repo.findById(id);
        if(eventuser.isPresent()){
            return eventuser.get();
        }
        throw new NoSuchElementException("Unable to find event with id: "+id);
    }

}
