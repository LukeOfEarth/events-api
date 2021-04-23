package com.events;


import com.events.Enums.UserStatus;
import com.events.eventsusers.EventUser;
import com.events.eventsusers.EventUserKey;
import com.events.eventsusers.EventUserService;
import com.events.users.User;
import com.events.users.UserService;
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
    @Autowired
    private final UserService userservice;
    @Autowired
    private final EventUserService eventuserservice;

    private final EventResourceAssembler assembler;

    public EventController(EventService service, UserService userservice, EventUserService eventuserservice, EventResourceAssembler assembler) {
        this.service = service;
        this.userservice = userservice;
        this.eventuserservice = eventuserservice;
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

    @PostMapping("events/{id}/join")
    public boolean joinEvent(@PathVariable Integer id){
        // TODO get USER ID from Auth.
        try {
            User user = userservice.get(1);
            Event event =  service.get(id);
            EventUserKey eventUserKey = new EventUserKey(event.getEventId(),user.getUserId());
            EventUser eventUser;
            try {
                eventUser = eventuserservice.get(eventUserKey);
            }catch(NoSuchElementException ex){
                eventUser = new EventUser(eventUserKey,event,user, UserStatus.JOINED);
            }

            if (eventUser.getStatus() == UserStatus.BANNED){
                return false;
            }else{
                eventUser.setStatus(UserStatus.JOINED);
                eventuserservice.save(eventUser);
                return true;
            }
        }
        catch (NoSuchElementException e){
            return false;
        }
    }

    @PostMapping("events/{id}/leave")
    public boolean leaveEvent(@PathVariable Integer id){
        // TODO get USER ID from Auth.
        try {
            User user = userservice.get(1);
            Event event =  service.get(id);
            EventUserKey eventUserKey = new EventUserKey(event.getEventId(),user.getUserId());
            EventUser eventUser;
            try {
                eventUser = eventuserservice.get(eventUserKey);
            }catch(NoSuchElementException ex){
                return false;
            }

            if (eventUser.getStatus() == UserStatus.BANNED){
                return false;
            }else{
                eventUser.setStatus(UserStatus.LEFT);
                eventuserservice.save(eventUser);
                return true;
            }
        }
        catch (NoSuchElementException e){
            return false;
        }
    }
}
