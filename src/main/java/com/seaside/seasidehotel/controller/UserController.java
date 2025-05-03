package com.seaside.seasidehotel.controller;

import com.seaside.seasidehotel.model.User;
import com.seaside.seasidehotel.service.RoleService;
import com.seaside.seasidehotel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    @GetMapping("/all-users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.FOUND);
    }

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUser(email));
    }

    @DeleteMapping("/delete/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') and #email == principal.username)")
    public ResponseEntity<String> deleteUser(@PathVariable("userId") String email) {
        userService.deleteUser(email);
        return ResponseEntity.ok("User has been deleted successfully!");
    }

    @PatchMapping
    public ResponseEntity<?> updatePassword(String password) {
        return null;
    }
}

