package com.events;

import com.events.eventsusers.EventsUsers;
import com.events.eventsusers.EventsUsersRepository;
import com.events.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Service
public class EventService {
    @Autowired
    private EventRepository repo;

    @Autowired
    private EventsUsersRepository eurepo;

    public List<Event> listAll() {
        return repo.findAll();
    }

    public void save(Event event) {
        repo.save(event);
    }

    public Event get(Integer id) {
        Optional<Event> event = repo.findById(id);
        if(event.isPresent()){
            return event.get();
        }
        throw new NoSuchElementException("Unable to find event with id: "+id);
    }
    
    public  void delete(Integer id) {
        repo.deleteById(id);
    }
}
