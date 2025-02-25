package com.seaside.seasidehotel.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "roles")
    private Collection<User> users = new HashSet<>();

    public Role(String name) {
        this.name = name;
    }

    public void assignRoleToUser(User user) {
        user.getRoles().add(this);
        this.getUsers().add(user);
    }

    public void stripUserOfRole(User user) {
        user.getRoles().remove(this);
        this.getUsers().remove(user);
    }

    public void stripAllUsersOfRole() {
        if (this.getUsers() != null) {
            List<User> roleUsers = this.getUsers().stream().toList();
            roleUsers.forEach(this :: stripUserOfRole); // equivalent of forEach(user -> stipUserOfRole(user))
        }
    }
}
