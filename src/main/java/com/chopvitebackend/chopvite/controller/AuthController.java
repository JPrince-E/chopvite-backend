package com.chopvitebackend.chopvite.controller;

import com.chopvitebackend.chopvite.dto.AuthResponseDTO;
import com.chopvitebackend.chopvite.dto.LoginDto;
import com.chopvitebackend.chopvite.dto.RegisterDto;
import com.chopvitebackend.chopvite.dto.UserResponse;
import com.chopvitebackend.chopvite.entity.Role;
import com.chopvitebackend.chopvite.entity.UserEntity;
import com.chopvitebackend.chopvite.repository.RoleRepository;
import com.chopvitebackend.chopvite.repository.UserRepository;
import com.chopvitebackend.chopvite.security.JwtProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);


    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private JwtProvider jwtProvider;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
                          RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDto loginDto) {
        Authentication authentication= authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()
                )
        );

        UserEntity user = userRepository.findByEmail(loginDto.getEmail()).get();
        UserResponse userResponse = getUserResponseFromUser(user);
        updateLastLogin(user);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtProvider.generateToken(authentication);
        return new ResponseEntity<>(new AuthResponseDTO(token, userResponse), HttpStatus.OK);
    }

    private void updateLastLogin(UserEntity user) {
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }

    private UserResponse getUserResponseFromUser(UserEntity user) {
        UserResponse userResponse = new UserResponse();
        try {
            BeanUtils.copyProperties(user, userResponse);
        } catch (Exception exception) {
            LOGGER.error("Error copying user properties");
        }
        return userResponse;
    }

    @PostMapping("register")
    public ResponseEntity<?> register(@RequestBody RegisterDto registerDto) {
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            return new ResponseEntity<>("Email already taken", HttpStatus.BAD_REQUEST);
        }

        UserEntity user = new UserEntity();
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        Role roles = roleRepository.findByName("USER").get();
        user.setRoles(Collections.singletonList(roles));

        user.setRoleName(roles.getName());
        userRepository.save(user);

        return new ResponseEntity<>("User registered successfully", HttpStatus.OK);
    }
}
