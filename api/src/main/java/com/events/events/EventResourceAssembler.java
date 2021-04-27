package com.events.events;

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
    private boolean isOwner;

    public EventResourceAssembler() {
        this.isOwner = false;
    }

    public void setIsOwner(boolean owner) {
        this.isOwner = owner;
    }

    @Override
    public EntityModel<Event> toModel(Event event) {
        //Get a link to this function, and to the list all events function
        Link selfLink = linkTo(methodOn(EventController.class).getEventById(event.getEventId(),null)).withSelfRel();
        Link eventsLink = linkTo(methodOn(EventController.class).list()).withRel("all_events");

        //Create an entity model for the event, with a self link and a link to get all events
        EntityModel<Event> eventEntityModel = EntityModel.of(event,
                selfLink,
                eventsLink);

        //Add conditional links based on event state
        List<Link> conditionalLinks = new LinkedList<>();

        //Get all links
        final Link deleteLink = linkTo(methodOn(EventController.class).deleteEvent(event.getEventId(),null)).withRel("delete");
        final Link cancelLink = linkTo(methodOn(EventController.class).cancelEvent(event.getEventId(),null)).withRel("cancel");
        final Link joinLink = linkTo(methodOn(EventController.class).joinEvent(event.getEventId(),null)).withRel("join");
        final Link leaveLink = linkTo(methodOn(EventController.class).leaveEvent(event.getEventId(),null)).withRel("leave");
        final Link updateLink = linkTo(methodOn(EventController.class).updateEvent(null, event.getEventId(),null)).withRel("update");
        final Link completeLink = linkTo(methodOn(EventController.class).markFinished(event.getEventId(),null)).withRel("complete");


        //Filter links based on event status
        switch (event.getStatus()) {
            case CANCELED:
                if(isOwner) conditionalLinks.add(completeLink);
            case FINISHED:
                if(isOwner) conditionalLinks.add(deleteLink);
                break;
            case UPCOMMING:
                if(isOwner) {
                    conditionalLinks.add(deleteLink);
                    conditionalLinks.add(cancelLink);
                    conditionalLinks.add(updateLink);
                    conditionalLinks.add(completeLink);
                }
                conditionalLinks.add(joinLink);
                conditionalLinks.add(leaveLink);
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