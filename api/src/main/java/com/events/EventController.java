package com.events;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.NoSuchElementException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class EventController {
    @Autowired
    private final EventService service;
    private final EventResourceAssembler assembler;

    public EventController(EventService service, EventResourceAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @GetMapping("events")
    public CollectionModel<EntityModel<Event>> list() {
        //Create a collection model of events
        CollectionModel<EntityModel<Event>> eventModels = assembler.toCollectionModel(service.listAll());

        //Create a link to this function and add to the collection model
        Link selfLink = linkTo(methodOn(EventController.class).list()).withSelfRel();
        eventModels.add(selfLink);

        //Return a collection model containing all events, and a link to this function
        return eventModels;
    }

    @GetMapping("events/{id}")
    public EntityModel<?> getEventById(@PathVariable Integer id) {
        try {
            Event event = service.get(id);
            return assembler.toModel(event);
        } catch (NoSuchElementException e) {
            return EntityModel.of(HttpStatus.NOT_FOUND);
        }
    }
  
    @PostMapping("newEvent")
    public void createEvent(@RequestBody Event event) {
        service.save(event);
    }

    @PostMapping("events/cancel/{id}")
    public void cancelEvent(@PathVariable Integer id) {
        service.cancel(id);
    }

    @PostMapping("events/delete/{id}") 
    public void deleteEvent(@PathVariable Integer id) {
        service.delete(id);
    }

    @PostMapping("events/complete/{id}")
    public void markFinished(@PathVariable Integer id) {
        service.complete(id);
    }

    @PostMapping("events/update/{id}")
    public void updateEvent(@RequestBody Event event,@PathVariable Integer id) {
        event.setEventId(id);
        service.update(event);
    }
}
