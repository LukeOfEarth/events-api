package com.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EventController {
    @Autowired
    private EventService service;

    @GetMapping("events")
    public List<Event> list() {
        return service.listAll();
    }

    @GetMapping("events/{id}")
    public Event getEventById(@PathVariable Integer id) { return service.get(id); }

    @PostMapping("newEvent")
    public void createEvent(@RequestBody Event event) { service.save(event); }
}
