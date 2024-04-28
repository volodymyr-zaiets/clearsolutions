package com.example.controller;

import com.example.model.User;
import com.example.payload.request.UserFindByBirthDateBetweenRequest;
import com.example.payload.request.UserUpdateRequest;
import com.example.payload.response.MessageResponse;
import com.example.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping()
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping("{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping("{id}")
    public User updateUserById(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        return userService.updateUserById(id, userUpdateRequest);
    }

    @DeleteMapping("{id}")
    public MessageResponse deleteUserById(@PathVariable Long id) {
        return userService.deleteUserById(id);
    }

    @GetMapping()
    public List<User> findUsersByBirthDateBetween(
            @Valid @RequestBody UserFindByBirthDateBetweenRequest userFindByBirthDateBetweenRequest
    ) {
        return userService.findUsersByBirthDateBetween(userFindByBirthDateBetweenRequest);
    }
}
