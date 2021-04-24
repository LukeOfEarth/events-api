package com.events.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.boot.web.servlet.error.ErrorController;

@RestController
public class FallbackErrorController implements ErrorController{
    private static final String PATH = "/error";

    @RequestMapping(value = PATH)
    public ResponseEntity<String> notFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error 404 - We couldn't find what you are looking for!");
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}
