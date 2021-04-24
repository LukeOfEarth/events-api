package com.events;

import com.events.Enums.EventStatus;
import com.events.eventsusers.EventUser;
import com.events.eventsusers.EventsUsersRepository;
import com.events.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class EventService {
    @Autowired
    private EventRepository repo;


    public List<Event> listAll() {
        return repo.findAll();
    }

    public Event save(Event event) {
        return repo.save(event);
    }

    public Event get(Integer id) {
        Optional<Event> event = repo.findById(id);
        if(event.isPresent()){
            return event.get();
        }
        throw new NoSuchElementException("Unable to find event with id: "+id);
    }
    
    public Event update( Event updatedEvent) {
        return save(updatedEvent);
    }

    public Event cancel(int eventID){
        Event event = get(eventID);
        event.setStatus(EventStatus.CANCELED);
        return update(event);
    }

    public void delete(int eventID){
        Event event = get(eventID);
        event.setStatus(EventStatus.DELETED);
        update(event);
    }

    public Event complete(int eventID){
        Event event = get(eventID);
        event.setStatus(EventStatus.FINISHED);
        return update(event);
    }
}
