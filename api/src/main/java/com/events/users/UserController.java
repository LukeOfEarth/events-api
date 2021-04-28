package com.events.users;

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
public class UserController {
    @Autowired
    private final UserService service;
    private final UserResourceAssembler assembler;

    public UserController(UserService service, UserResourceAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @GetMapping("users")
    public ResponseEntity<?> list() {
        assembler.setIsOwner(false);
        //Create a collection model of users
        CollectionModel<EntityModel<User>> userModels = assembler.toCollectionModel(service.listAll());

        //Create a link to this function and add to the collection model
        Link selfLink = linkTo(methodOn(UserController.class).list()).withSelfRel();
        userModels.add(selfLink);

        //Return a collection model containing all users, and a link to this function

        return ResponseEntity.ok(userModels);
    }

    @PostMapping("users")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        assembler.setIsOwner(false);
        EntityModel<User> userEntityModel = assembler.toModel(service.save(user));

        return ResponseEntity.created(userEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(userEntityModel);
    }

    @GetMapping("users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id, @AuthenticationPrincipal OidcUser principle) {
        try {
            String authUserEmail = principle.getEmail();

            User user = service.get(id);

            if (user.getEmail().equalsIgnoreCase(authUserEmail)) {
                assembler.setIsOwner(true);
            }else{
                assembler.setIsOwner(false);
            }
            return ResponseEntity.ok(assembler.toModel(user));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id, @AuthenticationPrincipal OidcUser principle) {
        try {
            String authUserEmail = principle.getEmail();

            User user = service.get(id);

            if (user.getEmail().equalsIgnoreCase(authUserEmail)) {
                service.delete(id);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Insufficient privileges");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PatchMapping("users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody User user, @AuthenticationPrincipal OidcUser principle) {
        try {
            String authUserEmail = principle.getEmail();

            if (user.getEmail().equalsIgnoreCase(authUserEmail)) {
                user.setUserId(id);
                assembler.setIsOwner(true);
                EntityModel<User> userEntityModel = assembler.toModel(service.update(user));
                return ResponseEntity.created(userEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(userEntityModel);
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Insufficient privileges");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}
