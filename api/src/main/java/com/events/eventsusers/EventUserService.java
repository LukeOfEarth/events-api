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



    public void save(EventUser eventuser) {
        repo.save(eventuser);
    }

    public EventUser get(EventUserKey id) {
        Optional<EventUser> event = repo.findById(id);
        if(event.isPresent()){
            return event.get();
        }
        throw new NoSuchElementException("Unable to find event with id: "+id);
    }

}
