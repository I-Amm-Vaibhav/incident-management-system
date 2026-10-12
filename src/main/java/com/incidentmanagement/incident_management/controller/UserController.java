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

    // If new user gives creds then create new account, or else should be able to login and verify password if username already exists
    // Also change the request dto to not accept role, by default everyone should be reporter, only admin can change the role based on user name
    @PostMapping
    public UserResponse createUser(@RequestBody CreateUserRequest request){
        return userService.createUser(request);
    }

    @GetMapping("/engineers")
    public List<UserResponse> engineerList(){
        return userService.userListRole(Role.ENGINEER);
    }
}
