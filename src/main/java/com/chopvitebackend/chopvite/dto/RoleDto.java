package com.chopvitebackend.chopvite.dto;

import com.chopvitebackend.chopvite.enums.Role;

public class RoleDto {

    private String name;

    // Constructors, getters, and setters

    public RoleDto() {
    }

    public RoleDto(String name) {
        this.name = name;
    }

    public Role toRole() {
        // Assuming you have a Role enum with the same name as the role in the DTO
        return Role.valueOf(name.toUpperCase());
    }

    // Other methods if needed
}
