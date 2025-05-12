package com.seaside.seasidehotel.service.impl;

import com.seaside.seasidehotel.exception.RoleNotFoundException;
import com.seaside.seasidehotel.exception.UserAlreadyExistsException;
import com.seaside.seasidehotel.exception.UserNotFoundException;
import com.seaside.seasidehotel.model.Role;
import com.seaside.seasidehotel.model.User;
import com.seaside.seasidehotel.repository.RoleRepository;
import com.seaside.seasidehotel.repository.UserRepository;
import com.seaside.seasidehotel.service.UserService;
import com.seaside.seasidehotel.utils.AppConstants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
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

        if (!userRepository.existsByEmail(email)) {
            throw new UserNotFoundException("User not found");
        }
        userRepository.deleteByEmail(email);
    }

    @Override
    public User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void updateResetToken(String email, String resetToken) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(1));
        userRepository.save(user);
    }

    @Override
    public User getUserByResetToken(String token) {
        return userRepository.findByResetToken(token)
                .orElseThrow(() -> new UserNotFoundException("Token is invalid or has expired"));
    }

    @Override
    public void increaseFailedAttempts(User user) {
        user.setNumberOfFailedAttempts(user.getNumberOfFailedAttempts() + 1);
        userRepository.save(user);
    }

    @Override
    public void resetFailedAttempts(User user) {
        user.setNumberOfFailedAttempts(null);
    }

    @Override
    public void lockAccount(User user) {
        user.setAccNonLocked(false);
        user.setLockTime(new Date());
        userRepository.save(user);
    }

    @Override
    public Boolean unlockAcc(User user) {

        long lockTime = user.getLockTime().getTime();
        long unlockTime = lockTime + AppConstants.UNLOCK_DURATION_TIME;

        long currentTime = System.currentTimeMillis();

        if (unlockTime < currentTime) {
            user.setAccNonLocked(true);
            user.setLockTime(null);
            user.setNumberOfFailedAttempts(0);
            userRepository.save(user);
            return true;
        }
        return false;
    }
}














