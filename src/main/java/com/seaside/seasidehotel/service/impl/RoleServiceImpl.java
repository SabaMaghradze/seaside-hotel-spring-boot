package com.seaside.seasidehotel.service.impl;

import com.seaside.seasidehotel.exception.RoleAlreadyExistsException;
import com.seaside.seasidehotel.exception.RoleNotFoundException;
import com.seaside.seasidehotel.exception.UserAlreadyExistsException;
import com.seaside.seasidehotel.exception.UserNotFoundException;
import com.seaside.seasidehotel.model.Role;
import com.seaside.seasidehotel.model.User;
import com.seaside.seasidehotel.repository.RoleRepository;
import com.seaside.seasidehotel.service.RoleService;
import com.seaside.seasidehotel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserService userService;

    @Override
    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role createRole(Role role) {

        String roleName = "ROLE_" + role.getName().toUpperCase();
        Role newRole = new Role(roleName);

        if (roleRepository.existsByName(roleName)) {
            throw new RoleAlreadyExistsException("Role already exists.");
        }
        return roleRepository.save(newRole);
    }

    @Override
    public void deleteRole(Long roleId) {
        if (!roleRepository.existsById(roleId)) {
            throw new RoleNotFoundException("Role not found");
        }
        this.stripAllUsersOfRole(roleId);
        roleRepository.deleteById(roleId);
    }

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));
    }

    @Override
    public User stripUserOfRole(String userId, Long roleId) {

        User user = userService.getUser(userId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found."));

        if (role.getUsers().contains(user.getEmail())) {
            role.stripUserOfRole(user);
            roleRepository.save(role);
            return user;
        }
        throw new UserNotFoundException("User not found");
    }

    @Override
    public User assignUserToRole(String userId, Long roleId) {

        User user = userService.getUser(userId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found."));

        if (user != null && user.getRoles().contains(role)) {
            throw new UserAlreadyExistsException(user.getFirstName() + " is already assigned the role.");
        }

        role.assignUserToRole(user);
        roleRepository.save(role);

        return user;
    }

    @Override
    public Role stripAllUsersOfRole(Long roleId) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));

        this.stripAllUsersOfRole(roleId);
        return roleRepository.save(role);
    }
}
