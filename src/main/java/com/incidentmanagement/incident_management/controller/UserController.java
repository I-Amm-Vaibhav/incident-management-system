package com.incidentmanagement.incident_management.controller;

import com.incidentmanagement.incident_management.dto.CreateUserRequest;
import com.incidentmanagement.incident_management.dto.UserResponse;
import com.incidentmanagement.incident_management.entity.User;
import com.incidentmanagement.incident_management.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping
    public UserResponse createUser(@RequestBody CreateUserRequest request){
        return userService.createUser(request);
    }
}
