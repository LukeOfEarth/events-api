package com.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

import com.events.Enums.EventStatus;

@RestController
public class EventController {
    @Autowired
    private EventService service;

    @GetMapping("events")
    public List<Event> list() {
        return service.listAll();
    }

    @GetMapping("events/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Integer id) {
        try {
            Event event = service.get(id);
            return new ResponseEntity<Event>(event, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<Event>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("newEvent")
    public void createEvent(@RequestBody Event event) {
        service.save(event);
    }

    @PostMapping("events/cancel/{id}")
    public boolean cancelEvent(@PathVariable Integer id) {
        try {
            Event event = service.get(id);
            event.setStatus(EventStatus.CANCELED);
            service.save(event);
            return true;
            
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
