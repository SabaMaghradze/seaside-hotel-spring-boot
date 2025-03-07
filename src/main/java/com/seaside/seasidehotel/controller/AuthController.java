package com.seaside.seasidehotel.controller;

import com.seaside.seasidehotel.exception.BadCredentialsException;
import com.seaside.seasidehotel.model.User;
import com.seaside.seasidehotel.request.LoginRequest;
import com.seaside.seasidehotel.response.JwtResponse;
import com.seaside.seasidehotel.security.jwt.JwtUtils;
import com.seaside.seasidehotel.security.user.UserDtls;
import com.seaside.seasidehotel.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        userService.registerUser(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        try {
            Authentication authentication = authManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwt(authentication);

            UserDtls userDetails = (UserDtls) authentication.getPrincipal();

            List<String> roles = userDetails
                    .getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority).toList();

            return ResponseEntity.ok(new JwtResponse(
                    userDetails.getId(),
                    userDetails.getEmail(),
                    jwt,
                    roles
            ));
        } catch (org.springframework.security.authentication.BadCredentialsException exc) {
            throw new BadCredentialsException("Invalid email or password");
        }
    }
}









