package com.seaside.seasidehotel.controller;

import com.seaside.seasidehotel.model.Role;
import com.seaside.seasidehotel.model.User;
import com.seaside.seasidehotel.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequiredArgsConstructor
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/all-roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.status(HttpStatus.FOUND).body(roleService.getRoles());
    }

    @PostMapping("/create")
    public ResponseEntity<String> createRole(@RequestBody Role theRole) {
        roleService.createRole(theRole);
        return ResponseEntity.ok("Role has been successfully created");
    }

    @DeleteMapping("/delete/{roleId}")
    public void deleteRole(@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
    }

    @GetMapping("/get-by-name/{name}")
    public ResponseEntity<Role> getByName(@PathVariable String name) {
        return ResponseEntity.status(HttpStatus.FOUND).body(roleService.findByName(name));
    }

    @DeleteMapping("/strip-all-users-of-role/{roleId}")
    public void stripAllUsersOfRole(@PathVariable Long roleId) {
        roleService.stripAllUsersOfRole(roleId);
    }

    @DeleteMapping("/strip-user-of-role")
    public ResponseEntity<User> stripUserOfRole(
            @RequestParam String userId,
            @RequestParam Long roleId) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(roleService.stripUserOfRole(userId, roleId));
    }

    @PostMapping("/assign-user-to-role")
    public ResponseEntity<User> assignUserToRole(
            @RequestParam String userId,
            @RequestParam Long roleId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.assignUserToRole(userId, roleId));
    }

}









