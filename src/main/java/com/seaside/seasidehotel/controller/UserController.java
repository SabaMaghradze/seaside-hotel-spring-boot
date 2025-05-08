package com.seaside.seasidehotel.controller;

import com.seaside.seasidehotel.model.User;
import com.seaside.seasidehotel.response.ApiResponse;
import com.seaside.seasidehotel.service.RoleService;
import com.seaside.seasidehotel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final ApiResponse apiResponse;

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

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> updatePassword(
            @RequestParam String currentPassword,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Principal principal
    ) {

        User user = userService.getUser(principal.getName());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "User not found"));
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Wrong Password"));
        }

        if (!password.equals(confirmPassword)) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Passwords do not match"));
        }

        user.setPassword(passwordEncoder.encode(password));
        userService.updateUser(user);

//        URI redirectUri = URI.create("/profile?message=Password+has+been+successfully+changed");
        return ResponseEntity.status(HttpStatus.FOUND)
                .body(new ApiResponse(true, "Password has been successfully changed"));
    }
}









