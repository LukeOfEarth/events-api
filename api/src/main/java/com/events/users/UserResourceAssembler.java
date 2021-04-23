package com.events.users;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
class UserResourceAssembler implements RepresentationModelAssembler<User, EntityModel<User>> {


    @Override
    public EntityModel<User> toModel(User user) {
        //Get a link to this function, and to the list all users function
        Link selfLink = linkTo(methodOn(UserController.class).getUserById(user.getUserId())).withSelfRel();
        Link usersLink = linkTo(methodOn(UserController.class).list()).withRel("all_users");

        //Create an entity model for the user, with a self link and a link to get all users
        return EntityModel.of(
                user,
                selfLink,
                usersLink
        );
    }

    @Override
    public CollectionModel<EntityModel<User>> toCollectionModel(Iterable<? extends User> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}