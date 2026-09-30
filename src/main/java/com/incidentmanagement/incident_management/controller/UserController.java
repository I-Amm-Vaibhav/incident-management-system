package com.incidentmanagement.incident_management.controller;

import com.incidentmanagement.incident_management.dto.CreateUserRequest;
import com.incidentmanagement.incident_management.dto.UserResponse;
import com.incidentmanagement.incident_management.entity.Role;
import com.incidentmanagement.incident_management.entity.User;
import com.incidentmanagement.incident_management.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/engineers")
    public List<UserResponse> engineerList(){
        return userService.userListRole(Role.ENGINEER);
    }
}
