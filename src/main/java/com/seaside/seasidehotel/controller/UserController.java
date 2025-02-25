package com.seaside.seasidehotel.controller;

import com.seaside.seasidehotel.exception.UserNotFoundException;
import com.seaside.seasidehotel.model.User;
import com.seaside.seasidehotel.service.RoleService;
import com.seaside.seasidehotel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    @GetMapping("/all-users")
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.FOUND);
    }

    @GetMapping("/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        return new ResponseEntity<>(userService.getUser(email), HttpStatus.FOUND);
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable("userId") String email) {
        userService.deleteUser(email);
        return ResponseEntity.ok("User has been deleted successfully!");
    }

}

