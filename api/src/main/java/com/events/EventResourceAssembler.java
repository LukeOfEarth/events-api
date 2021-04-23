package com.events;

import com.events.Enums.EventStatus;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
class EventResourceAssembler implements RepresentationModelAssembler<Event, EntityModel<Event>> {

    @Override
    public EntityModel<Event> toModel(Event event) {
        //Get a link to this function, and to the list all events function
        Link selfLink = linkTo(methodOn(EventController.class).getEventById(event.getEventId())).withSelfRel();
        Link eventsLink = linkTo(methodOn(EventController.class).list()).withRel("all_events");

        //Create an entity model for the event, with a self link and a link to get all events
        EntityModel<Event> eventEntityModel = EntityModel.of(   event,
                                                                selfLink,
                                                                eventsLink);

        //Add conditional links based on event state
        List<Link> conditionalLinks = new LinkedList<>();

        //TODO add filtering based on if user making request has authority
        final Link deleteLink = linkTo(methodOn(EventController.class).deleteEvent(event.getEventId())).withRel("delete");
        final Link cancelLink = linkTo(methodOn(EventController.class).cancelEvent(event.getEventId())).withRel("cancel");
        final Link joinLink = linkTo(methodOn(EventController.class).joinEvent(event.getEventId())).withRel("join");
        final Link leaveLink = linkTo(methodOn(EventController.class).leaveEvent(event.getEventId())).withRel("leave");
        final Link updateLink = linkTo(methodOn(EventController.class).updateEvent(null,event.getEventId())).withRel("update");
        final Link completeLink = linkTo(methodOn(EventController.class).markFinished(event.getEventId())).withRel("complete");


        switch (event.getStatus()){
            case CANCELED:
                conditionalLinks.add(completeLink);
            case FINISHED:
                conditionalLinks.add(deleteLink);
                break;
            case UPCOMMING:
                conditionalLinks.add(deleteLink);
                conditionalLinks.add(cancelLink);
                conditionalLinks.add(joinLink);
                conditionalLinks.add(leaveLink);
                conditionalLinks.add(updateLink);
                conditionalLinks.add(completeLink);
                break;
            case DELETED:
                //Don't want additional links if deleted
                break;
        }
        return eventEntityModel.add(conditionalLinks);
    }

    @Override
    public CollectionModel<EntityModel<Event>> toCollectionModel(Iterable<? extends Event> events) {
        return RepresentationModelAssembler.super.toCollectionModel(events);
    }
}