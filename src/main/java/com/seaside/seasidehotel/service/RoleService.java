package com.seaside.seasidehotel.service;

import com.seaside.seasidehotel.exception.RoleNotFoundException;
import com.seaside.seasidehotel.model.Role;
import com.seaside.seasidehotel.model.User;

import java.util.List;

public interface RoleService {

    List<Role> getRoles();

    Role createRole(Role role);

    void deleteRole(Long id);

    Role findByName(String name);

    User stripUserOfRole(String userId, Long roleId);

    User assignUserToRole(String userId, Long roleId);

    Role stripAllUsersOfRole(Long roleId);
}
