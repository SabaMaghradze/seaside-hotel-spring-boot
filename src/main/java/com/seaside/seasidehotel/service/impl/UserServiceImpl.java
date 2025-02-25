package com.seaside.seasidehotel.service.impl;

import com.seaside.seasidehotel.exception.RoleNotFoundException;
import com.seaside.seasidehotel.exception.UserAlreadyExistsException;
import com.seaside.seasidehotel.exception.UserNotFoundException;
import com.seaside.seasidehotel.model.Role;
import com.seaside.seasidehotel.model.User;
import com.seaside.seasidehotel.repository.RoleRepository;
import com.seaside.seasidehotel.repository.UserRepository;
import com.seaside.seasidehotel.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException(user.getEmail() + " already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RoleNotFoundException("Role ROLE_USER not found."));
        user.setRoles(Collections.singletonList(userRole));

        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    @Override
    public void deleteUser(String email) {
        userRepository.deleteByEmail(email);
    }

    @Override
    public User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
