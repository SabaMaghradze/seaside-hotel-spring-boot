package com.seaside.seasidehotel.service.impl;

import com.seaside.seasidehotel.exception.RoleAlreadyExistsException;
import com.seaside.seasidehotel.exception.RoleNotFoundException;
import com.seaside.seasidehotel.exception.UserNotFoundException;
import com.seaside.seasidehotel.model.Role;
import com.seaside.seasidehotel.model.User;
import com.seaside.seasidehotel.repository.RoleRepository;
import com.seaside.seasidehotel.service.RoleService;
import com.seaside.seasidehotel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
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
        return roleRepository.save(role);
    }

    @Override
    public void deleteRole(Long roleId) {
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
        Optional<Role> role = roleRepository.findById(roleId);

        if (role.isPresent() && role.get().getUsers().contains(user.getEmail())) {
            role.get().stripUserOfRole(user);
            roleRepository.save(role.get());
            return user;
        } else if (!role.isPresent()) {
            throw new RoleNotFoundException("Role not found");
        }
        throw new UserNotFoundException("User not found");
    }

    @Override
    public User asignRoleToUser(Long userId, Long roleId) {
        return null;
    }

    @Override
    public Role stripAllUsersOfRole(Long roleId) {
        return null;
    }
}
