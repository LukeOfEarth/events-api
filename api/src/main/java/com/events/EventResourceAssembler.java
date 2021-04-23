package com.events;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
class EventResourceAssembler implements RepresentationModelAssembler<Event, EntityModel<Event>> {

    @Override
    public EntityModel<Event> toModel(Event event) {
        //Get a link to this function, and to the list all events function
        Link selfLink = linkTo(methodOn(EventController.class).getEventById(event.getEventId())).withSelfRel();
        Link usersLink = linkTo(methodOn(EventController.class).list()).withRel("all_events");

        //Create an entity model for the event, with a self link and a link to get all events
        return EntityModel.of(
                event,
                selfLink,
                usersLink
        );
    }

    @Override
    public CollectionModel<EntityModel<Event>> toCollectionModel(Iterable<? extends Event> events) {
        return RepresentationModelAssembler.super.toCollectionModel(events);
    }
}