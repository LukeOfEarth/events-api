package com.events.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
public class UserController {
    @Autowired
    private UserService service;

    @GetMapping("users")
    public List<User> list() {
        return service.listAll();
    }

    @GetMapping("users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        try {
            User user = service.get(id);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping("newUser")
    public void createUser(@RequestBody User user) { service.save(user); }
}
