package com.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {
    @Autowired
    private EventRepository repo;

    public List<Event> listAll() {
        return repo.findAll();
    }

    public void save(Event event) {
        repo.save(event);
    }

    public Event get(Integer id) {
        return repo.findById(id).get();
    }

    public  void delete(Integer id) {
        repo.deleteById(id);
    }
}