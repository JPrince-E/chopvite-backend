package com.chopvitebackend.chopvite.dto;

import com.chopvitebackend.chopvite.enums.Role;

public class UserContext {

    private static final ThreadLocal<Role> userRole = new ThreadLocal<>();

    public static Role getUserRole() {
        return userRole.get();
    }

    public static void setUserRole(Role role) {
        userRole.set(role);
    }

    public static void clear() {
        userRole.remove();
    }
}
