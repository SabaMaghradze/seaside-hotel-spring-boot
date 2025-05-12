package com.seaside.seasidehotel.service;

import com.seaside.seasidehotel.model.User;

import java.util.List;

public interface UserService {

    User registerUser(User user);

    List<User> getAllUsers();

    void deleteUser(String email);

    User getUser(String email);

    User getUserById(Long userId);

    User updateUser(User user);

    void updateResetToken(String email, String resetToken);

    User getUserByResetToken(String token);

    void increaseFailedAttempts(User user);

    void lockAccount(User user);

    Boolean unlockAcc(User user);

    void resetFailedAttempts(User user);

}
