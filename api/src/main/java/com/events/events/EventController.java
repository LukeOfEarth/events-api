package com.events.events;


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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
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

    private final EventResourceAssembler eventResourceAssembler;

    public EventController(EventService service, UserService userservice, EventUserService eventuserservice, EventResourceAssembler eventResourceAssembler) {
        this.service = service;
        this.userservice = userservice;
        this.eventuserservice = eventuserservice;
        this.eventResourceAssembler = eventResourceAssembler;
    }

    private String getAuthUserEmail(OidcUser principle){
        return principle.getAttributes().get("email").toString();
    }
    private boolean getIsEventOwner(Event event, OidcUser principle){
        String authUserEmail = getAuthUserEmail(principle);

        User owner  = userservice.get(event.getOwnerId());

        return owner.getEmail().equalsIgnoreCase(authUserEmail);
    }

    @GetMapping("events")
    public ResponseEntity<?> list() {
        eventResourceAssembler.setIsOwner(false);
        //Create a collection model of events
        CollectionModel<EntityModel<Event>> eventModels = eventResourceAssembler.toCollectionModel(service.listAll());

        //Create a link to this function and add to the collection model
        Link selfLink = linkTo(methodOn(EventController.class).list()).withSelfRel();
        eventModels.add(selfLink);

        //Return a collection model containing all events, and a link to this function
        return ResponseEntity.ok(eventModels);
    }

    @GetMapping("events/{id}")
    public ResponseEntity<?> getEventById(@PathVariable Integer id,@AuthenticationPrincipal OidcUser principle) {
        try {

            Event event = service.get(id);

            eventResourceAssembler.setIsOwner(getIsEventOwner(event,principle));

            //Make true if is owner, used to filter links
            return ResponseEntity.ok(eventResourceAssembler.toModel(event));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("newEvent")
    public ResponseEntity<?> createEvent(@RequestBody Event event,@AuthenticationPrincipal OidcUser principle) {
        //User is automatically owner if they create a new event
        eventResourceAssembler.setIsOwner(getIsEventOwner(event,principle));
        EntityModel<Event> eventEntityModel = eventResourceAssembler.toModel(service.save(event));

        return ResponseEntity.created(eventEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(eventEntityModel);
    }

    @PostMapping("events/cancel/{id}")
    public ResponseEntity<?> cancelEvent(@PathVariable Integer id,@AuthenticationPrincipal OidcUser principle) {
        Event event = service.get(id);
        boolean isOwner = getIsEventOwner(event,principle);
        eventResourceAssembler.setIsOwner(isOwner);
        if(isOwner) {
            EntityModel<Event> eventEntityModel = eventResourceAssembler.toModel(service.cancel(id));
            return ResponseEntity.created(eventEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(eventEntityModel);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Insufficient privileges");
    }

    @PostMapping("events/delete/{id}")
    public ResponseEntity<Object> deleteEvent(@PathVariable Integer id,@AuthenticationPrincipal OidcUser principle) {
        Event event = service.get(id);
        boolean isOwner = getIsEventOwner(event,principle);
        eventResourceAssembler.setIsOwner(isOwner);
        if(isOwner) {
            service.delete(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Insufficient privileges");
    }

    @PostMapping("events/complete/{id}")
    public ResponseEntity<?> markFinished(@PathVariable Integer id,@AuthenticationPrincipal OidcUser principle) {
        Event event = service.get(id);
        boolean isOwner = getIsEventOwner(event,principle);
        eventResourceAssembler.setIsOwner(isOwner);
        if(isOwner) {
            EntityModel<Event> eventEntityModel = eventResourceAssembler.toModel(service.complete(id));
            return ResponseEntity.created(eventEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(eventEntityModel);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Insufficient privileges");
    }

    @PostMapping("events/update/{id}")
    public ResponseEntity<?> updateEvent(@RequestBody Event event, @PathVariable Integer id,@AuthenticationPrincipal OidcUser principle) {
        Event eventOriginal = service.get(id);
        boolean isOwner = getIsEventOwner(eventOriginal,principle);
        eventResourceAssembler.setIsOwner(isOwner);
        if(isOwner) {
            event.setEventId(id);
            EntityModel<Event> eventEntityModel = eventResourceAssembler.toModel(service.update(event));

            return ResponseEntity.created(eventEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(eventEntityModel);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Insufficient privileges");
    }

    @PostMapping("events/{id}/join")
    public ResponseEntity<?> joinEvent(@PathVariable Integer id,@AuthenticationPrincipal OidcUser principle) {
        try {
            User user = userservice.getByEmail(getAuthUserEmail(principle));
            Event event = service.get(id);
            EventUserKey eventUserKey = new EventUserKey(event.getEventId(), user.getUserId());
            EventUser eventUser;
            try {
                eventUser = eventuserservice.get(eventUserKey);
            } catch (NoSuchElementException ex) {
                eventUser = new EventUser(eventUserKey, event, user, UserStatus.JOINED);
            }

            if (eventUser.getStatus() == UserStatus.BANNED) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User has been banned from the event.");
            } else {
                eventUser.setStatus(UserStatus.JOINED);
                eventuserservice.save(eventUser);
                EntityModel<Event> eventEntityModel = eventResourceAssembler.toModel(event);
                return ResponseEntity.ok(eventEntityModel);
            }
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("events/{eventid}/unban/{useridtounban}")
    public ResponseEntity<?> unbanUser(@PathVariable Integer eventid, @PathVariable Integer useridtounban,@AuthenticationPrincipal OidcUser principle) {
        try {
            User user = userservice.getByEmail(getAuthUserEmail(principle));

            User usertoban = userservice.get(useridtounban);
            Event event = service.get(eventid);
            if (event.getOwnerId() == user.getUserId()) {
                EventUserKey eventUserKey = new EventUserKey(event.getEventId(), usertoban.getUserId());
                EventUser eventUser;
                eventUser = eventuserservice.get(eventUserKey);
                if (eventUser.getStatus() == UserStatus.BANNED) {
                    eventUser.setStatus(UserStatus.JOINED);
                    eventuserservice.save(eventUser);

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
    public ResponseEntity<?> banUser(@PathVariable Integer eventid, @PathVariable Integer useridtoban,@AuthenticationPrincipal OidcUser principle) {
        try {
            User user = userservice.getByEmail(getAuthUserEmail(principle));

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
    public ResponseEntity<?> leaveEvent(@PathVariable Integer id,@AuthenticationPrincipal OidcUser principle) {
        try {
            User user = userservice.getByEmail(getAuthUserEmail(principle));
            Event event = service.get(id);
            EventUserKey eventUserKey = new EventUserKey(event.getEventId(), user.getUserId());
            EventUser eventUser;
            try {
                eventUser = eventuserservice.get(eventUserKey);
            } catch (NoSuchElementException ex) {
                // Has not joined thus cannot leave
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
            }

            if (eventUser.getStatus() == UserStatus.BANNED) {
                // Prevent BANNED user from changing own status
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User has been banned from the event.");
            } else {
                eventUser.setStatus(UserStatus.LEFT);
                eventuserservice.save(eventUser);
                EntityModel<Event> eventEntityModel = eventResourceAssembler.toModel(event);
                return ResponseEntity.ok(eventEntityModel);
            }
        } catch (NoSuchElementException e) {
            // Event or user does not exist
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
