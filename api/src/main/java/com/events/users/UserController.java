package com.events.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
public class UserController {
    @Autowired
    private UserService service;

    @GetMapping("users")
    public CollectionModel<EntityModel<User>> list() {

        List<EntityModel<User>> users = service.listAll().stream()
                .map(user -> getUserById(id))
                .collect(Collectors.toList());

        return new CollectionModel<>(users,
                linkTo(methodOn(UserController.class).list()).withSelfRel());
    }

    @PostMapping("users")
    public void createUser(@RequestBody User user) { service.save(user); }

    @GetMapping("users/{id}")
    public EntityModel<User> getUserById(@PathVariable Integer id) {
        try {
            User user = service.get(id);
            return new EntityModel<>(
                    user,
                    linkTo(methodOn(UserController.class).getUserById(id)).withSelfRef(),
                    linkTo(methodOn(UserController.class).list()).withRel("users")
            );
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("users/{id}")
    public void deleteUser(@PathVariable Integer id) { service.delete(id); }
}
