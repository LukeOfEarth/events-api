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
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> list() {
        //Create a collection model of events
        CollectionModel<EntityModel<Event>> eventModels = assembler.toCollectionModel(service.listAll());

        //Create a link to this function and add to the collection model
        Link selfLink = linkTo(methodOn(EventController.class).list()).withSelfRel();
        eventModels.add(selfLink);

        //Return a collection model containing all events, and a link to this function
        return ResponseEntity.ok(eventModels);
    }

    @GetMapping("events/{id}")
    public ResponseEntity<?> getEventById(@PathVariable Integer id) {
        try {
            Event event = service.get(id);
            return ResponseEntity.ok(assembler.toModel(event));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
  
    @PostMapping("newEvent")
    public ResponseEntity<?> createEvent(@RequestBody Event event) {
        EntityModel<Event> eventEntityModel = assembler.toModel(service.save(event));

        return ResponseEntity.created(eventEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(eventEntityModel);
    }

    @PostMapping("events/cancel/{id}")
    public ResponseEntity<?> cancelEvent(@PathVariable Integer id) {
        EntityModel<Event> eventEntityModel = assembler.toModel(service.cancel(id));

        return ResponseEntity.created(eventEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(eventEntityModel);
    }

    @PostMapping("events/delete/{id}") 
    public ResponseEntity<Object> deleteEvent(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("events/complete/{id}")
    public ResponseEntity<?> markFinished(@PathVariable Integer id) {
        EntityModel<Event> eventEntityModel = assembler.toModel(service.complete(id));

        return ResponseEntity.created(eventEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(eventEntityModel);
    }

    @PostMapping("events/update/{id}")
    public ResponseEntity<?> updateEvent(@RequestBody Event event, @PathVariable Integer id) {
        event.setEventId(id);
        EntityModel<Event> eventEntityModel = assembler.toModel(service.update(event));

        return ResponseEntity.created(eventEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(eventEntityModel);
    }

    @PostMapping("events/{id}/join")
    public ResponseEntity<?> joinEvent(@PathVariable Integer id){
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
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User has been banned from the event.");
            }else{
                eventUser.setStatus(UserStatus.JOINED);
                eventuserservice.save(eventUser);
                EntityModel<Event> eventEntityModel = assembler.toModel(event);
                return ResponseEntity.ok(eventEntityModel);
            }
        }
        catch (NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("events/{eventid}/unban/{useridtounban}")
    public ResponseEntity<?> unbanUser(@PathVariable Integer eventid, @PathVariable Integer useridtounban) {
        try {
            // TODO get USER ID from Auth.
            User user = userservice.get(1);

            User usertoban = userservice.get(useridtounban);
            Event event = service.get(eventid);
            if (event.getOwnerId() == user.getUserId()) {
                EventUserKey eventUserKey = new EventUserKey(event.getEventId(), usertoban.getUserId());
                EventUser eventUser;
                eventUser = eventuserservice.get(eventUserKey);
                if (eventUser.getStatus() == UserStatus.BANNED) {
                    eventUser.setStatus(UserStatus.JOINED);
                    eventuserservice.save(eventUser);

                    //TODO replace this with something more meaningful if required
                    return ResponseEntity.ok().build();
                } else {
                    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("You cannot unban a user that isn't banned");
                }
            } else {
                // User is not event owner
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User does not have the authority to unban a user from this event.");
            }
        } catch (NoSuchElementException e) {
            // Event or User does not exist
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("events/{eventid}/ban/{useridtoban}")
    public ResponseEntity<?> banUser(@PathVariable Integer eventid, @PathVariable Integer useridtoban) {
        try {
            // TODO get USER ID from Auth.
            User user = userservice.get(1);

            User usertoban = userservice.get(useridtoban);
            Event event = service.get(eventid);
            if (event.getOwnerId() == user.getUserId()) {
                EventUserKey eventUserKey = new EventUserKey(event.getEventId(), usertoban.getUserId());
                EventUser eventUser;
                try {
                    eventUser = eventuserservice.get(eventUserKey);
                    eventUser.setStatus(UserStatus.BANNED);
                } catch (NoSuchElementException ex) {
                    eventUser = new EventUser(eventUserKey, event, user, UserStatus.BANNED);
                }
                eventuserservice.save(eventUser);
                //TODO replace this with something more meaningful if required
                return ResponseEntity.ok().build();
            } else {
                // User is not event owner
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User does not have the authority to ban a user from this event");
            }
        } catch (NoSuchElementException e) {
            // Event or user does not exist
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("events/{id}/leave")
    public ResponseEntity<?> leaveEvent(@PathVariable Integer id){
        // TODO get USER ID from Auth.
        try {
            User user = userservice.get(1);
            Event event =  service.get(id);
            EventUserKey eventUserKey = new EventUserKey(event.getEventId(),user.getUserId());
            EventUser eventUser;
            try {
                eventUser = eventuserservice.get(eventUserKey);
            }catch(NoSuchElementException ex){
                // Has not joined thus cannot leave
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
            }

            if (eventUser.getStatus() == UserStatus.BANNED){
                // Prevent BANNED user from changing own status
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User has been banned from the event.");
            }else{
                eventUser.setStatus(UserStatus.LEFT);
                eventuserservice.save(eventUser);
                EntityModel<Event> eventEntityModel = assembler.toModel(event);
                return ResponseEntity.ok(eventEntityModel);
            }
        }catch (NoSuchElementException e) {
            // Event or user does not exist
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
