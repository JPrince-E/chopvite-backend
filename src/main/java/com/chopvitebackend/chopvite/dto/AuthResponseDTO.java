package com.chopvitebackend.chopvite.dto;

import lombok.Data;

@Data
public class AuthResponseDTO {
    private String accessToken;
    private String tokenType = "Bearer";
    private UserResponse user;

    public AuthResponseDTO(String accessToken, UserResponse user) {
        this.accessToken = accessToken;
        this.user = user;
    }
}
