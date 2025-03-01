package com.seaside.seasidehotel.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {

    private Long id;

    private String email;

    private String token;

    private String type = "Bearer";

    private List<String> roles;

    public JwtResponse(Long id, String email, String jwt, List<String> roles) {
        this.id = id;
        this.email = email;
        this.token = jwt;
        this.roles = roles;
    }
}
