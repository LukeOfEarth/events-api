package com.events.users;

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
public class UserController {
    @Autowired
    private final UserService service;
    private final UserResourceAssembler assembler;

    public UserController(UserService service, UserResourceAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @GetMapping("users")
    public CollectionModel<EntityModel<User>> list() {
        //Create a collection model of users
        CollectionModel<EntityModel<User>> userModels = assembler.toCollectionModel(service.listAll());

        //Create a link to this function and add to the collection model
        Link selfLink = linkTo(methodOn(UserController.class).list()).withSelfRel();
        userModels.add(selfLink);

        //Return a collection model containing all users, and a link to this function
        return userModels;
    }

    @PostMapping("users")
    public void createUser(@RequestBody User user) { service.save(user); }

    @GetMapping("users/{id}")
    public EntityModel<?> getUserById(@PathVariable Integer id) {
        try {
            User user = service.get(id);
            return assembler.toModel(user);
        } catch (NoSuchElementException e) {
            return EntityModel.of(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("users/{id}")
    public void deleteUser(@PathVariable Integer id) { service.delete(id); }

    @PatchMapping("users/{id}")
    public void updateUser(@PathVariable Integer id, @RequestBody User user){
        user.setUserId(id);
        service.update(user);
    }

}
