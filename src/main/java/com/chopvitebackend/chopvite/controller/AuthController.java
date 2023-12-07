package com.chopvitebackend.chopvite.controller;

import com.chopvitebackend.chopvite.dto.*;
import com.chopvitebackend.chopvite.enums.Role;
import com.chopvitebackend.chopvite.service.AuthService;
import com.chopvitebackend.chopvite.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.NotAcceptableStatusException;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDto loginDto) {
        AuthResponseDTO authResponse = authService.authenticateUser(loginDto);
        return ResponseEntity.ok()
                .body(new Response(true, "User logged in successfully", authResponse));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterDto registerDto,
                                          @RequestHeader(name = "Role", defaultValue = "USER") String userRole) {
        try {
            // Assuming you want to get the user role from the header
            Role role = Role.valueOf(userRole.toUpperCase());

            URI location = authService.registerUser(registerDto, role);

            LoginDto loginDto = new LoginDto();
            if (!StringUtil.isBlank(registerDto.getEmail())) {
                loginDto.setEmail(registerDto.getEmail());
            }

            loginDto.setPassword(registerDto.getPassword());
            AuthResponseDTO authResponse = authService.authenticateUser(loginDto);

            return ResponseEntity.created(location)
                    .body(new Response(true, "User registered successfully", authResponse));
        } catch (NotAcceptableStatusException e) {
            // Handle the case where phone number or email already exists
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new Response(false, e.getMessage()));
        }
    }
}
