package com.miniioc.examples;

import com.miniioc.core.annotation.Controller;
import com.miniioc.core.annotation.Get;
import com.miniioc.core.annotation.Scope;
import com.miniioc.core.annotation.ScopeType;

@Controller
@Scope(ScopeType.PROTOTYPE)
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Get("/users/hello")
    public String hello() {
        return "Hello, " + userService.getUser();
    }

    @Get("/lol")
    public String lol() {
        return "Hello, lol";
    }
}
