package com.seaside.seasidehotel.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

// all of these return plain text rather than JSON, needs restructuring to e.g.:

//ApiResponse response = new ApiResponse(false, exc.getMessage());
//return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final HttpServletResponse httpServletResponse;

    public GlobalExceptionHandler(HttpServletResponse httpServletResponse) {
        this.httpServletResponse = httpServletResponse;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException exc) {

        Map<String, String> errors = new HashMap<>();
        exc.getBindingResult().getFieldErrors().forEach(err ->
                errors.put(err.getField(), err.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException exc) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exc.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException exc) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exc.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException exc) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exc.getMessage());
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<String> handleRoleNotFound(RoleNotFoundException exc) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exc.getMessage());
    }

    @ExceptionHandler(RoleAlreadyExistsException.class)
    public ResponseEntity<String> handleRoleAlreadyExistsException(RoleAlreadyExistsException exc) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exc.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", httpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", ex.getMessage());
        body.put("path", request.getServletPath());
        return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<?> handleLockedException(LockedException ex, HttpServletRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Account Locked");
        body.put("message", ex.getMessage());
        body.put("path", request.getServletPath());
        return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(body);
    }

}








