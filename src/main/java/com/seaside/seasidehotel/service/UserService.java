package com.seaside.seasidehotel.service;

import com.seaside.seasidehotel.model.User;

import java.util.List;

public interface UserService {

    User registerUser(User user);

    List<User> getAllUsers();

    void deleteUser(String email);

    User getUser(String email);

}
