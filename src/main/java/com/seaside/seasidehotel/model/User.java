package com.seaside.seasidehotel.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String resetToken;

    private LocalDateTime resetTokenExpiry;

    @Column(nullable = false)
    private Boolean isEnabled;

    @Column(nullable = false)
    private Boolean accNonLocked;

    @Column(nullable = false)
    private Integer numberOfFailedAttempts;

    private Date lockTime;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH})
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles = new HashSet<>();
}
