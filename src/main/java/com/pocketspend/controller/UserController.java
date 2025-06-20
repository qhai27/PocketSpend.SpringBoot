package com.pocketspend.controller;

import com.pocketspend.model.User;
import com.pocketspend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody User user) {
        User createdUser = userService.signup(user.getUsername(), user.getPassword());
        return ResponseEntity.ok(createdUser);
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {
        User authenticatedUser = userService.login(user.getUsername(), user.getPassword());
        return ResponseEntity.ok(authenticatedUser);
    }
}