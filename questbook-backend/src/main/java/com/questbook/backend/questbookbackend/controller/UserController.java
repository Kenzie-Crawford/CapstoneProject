package com.questbook.backend.questbookbackend.controller;
import com.questbook.backend.questbookbackend.model.User;
import com.questbook.backend.questbookbackend.service.UserService;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }
    @PostMapping("/register")
    public User registerUser(@RequestBody User user){
        return userService.registerUser(user);
    }
    @PostMapping("/login")
    public User loginUser(@RequestBody User user){
        return userService.loginUser(user.getUsername(),user.getPassword());
    }
    @GetMapping("/{id}")
    public User getUser(@PathVariable int id){
        return userService.getUserById(id);
    }
}
