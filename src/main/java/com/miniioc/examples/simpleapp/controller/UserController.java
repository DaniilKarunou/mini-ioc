package com.miniioc.examples.simpleapp.controller;

import com.miniioc.core.annotation.http.GetMapping;
import com.miniioc.core.annotation.http.PostMapping;
import com.miniioc.core.annotation.injection.Controller;
import com.miniioc.core.annotation.request.RequestParam;
import com.miniioc.core.annotation.request.PathVariable;
import com.miniioc.core.annotation.request.RequestBody;
import com.miniioc.core.annotation.injection.Scope;
import com.miniioc.core.annotation.injection.ScopeType;

import com.miniioc.examples.simpleapp.service.UserService;
import com.miniioc.examples.simpleapp.model.User;

import java.util.List;

@Controller
@Scope(ScopeType.SINGLETON)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable("id") int id) {
        return userService.getUserById(id);
    }

    @PostMapping("/users")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user.getName(), user.getEmail());
    }

    @PostMapping("/users/delete")
    public String deleteUser(@RequestParam("id") int id) {
        boolean removed = userService.deleteUser(id);
        return removed ? "Deleted user with id " + id : "User not found";
    }
}