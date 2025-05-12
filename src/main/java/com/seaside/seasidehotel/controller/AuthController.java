package com.seaside.seasidehotel.controller;

import com.seaside.seasidehotel.exception.UserNotFoundException;
import com.seaside.seasidehotel.model.User;
import com.seaside.seasidehotel.request.LoginRequest;
import com.seaside.seasidehotel.response.ApiResponse;
import com.seaside.seasidehotel.response.JwtResponse;
import com.seaside.seasidehotel.security.jwt.JwtUtils;
import com.seaside.seasidehotel.security.user.UserDtls;
import com.seaside.seasidehotel.service.UserService;
import com.seaside.seasidehotel.utils.AppConstants;
import com.seaside.seasidehotel.utils.CommonUtils;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;
    private final ApiResponse apiResponse;
    private final CommonUtils commonUtils;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        userService.registerUser(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        String email = loginRequest.getEmail();
        User user = userService.getUser(email);

        if (user == null) {
            throw new BadCredentialsException("User not found");
        }

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

            userService.resetFailedAttempts(user);

            return ResponseEntity.ok(new JwtResponse(
                    userDetails.getId(),
                    userDetails.getEmail(),
                    jwt,
                    roles
            ));

        } catch (AuthenticationException exception) {
            if (user.getIsEnabled()) {
                if (user.getAccNonLocked()) {
                    if (user.getNumberOfFailedAttempts() < AppConstants.ATTEMPT_COUNT) {
                        userService.increaseFailedAttempts(user);
                        throw new BadCredentialsException("Incorrect Credentials, Please Try Again");
                    } else {
                        userService.lockAccount(user);
                        throw new LockedException("Your account has been locked, failed attempt N.3");
                    }
                } else {
                    if (userService.unlockAcc(user)) {
                        throw new LockedException("Your account is unlocked, please try again.");
                    } else {
                        throw new LockedException("Your account is locked, please try again later");
                    }
                }
            } else {
                throw new LockedException("Your account is inactive");
            }
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> sendResetLink(@RequestParam String email, HttpServletRequest req) throws MessagingException, UnsupportedEncodingException {

        User user = userService.getUser(email);

        String resetToken = UUID.randomUUID().toString();
        userService.updateResetToken(email, resetToken);
        String url = commonUtils.generateUrl(req) + "/reset-password?token=" + resetToken;

        Boolean sendMail = commonUtils.sendMail(url, email);

        if (sendMail) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, "The reset link has been sent, please check your email"));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Failed to send email"));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestParam String token,
            @RequestParam String password,
            @RequestParam String confirmPassword
    ) throws UserNotFoundException {

        if (!password.equals(confirmPassword)) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Passwords do not match"));
        }

        User user = userService.getUserByResetToken(token);

        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Token has expired"));
        }

        user.setPassword(passwordEncoder.encode(password));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userService.updateUser(user);

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, "Password has been reset successfully"));

    }
}















