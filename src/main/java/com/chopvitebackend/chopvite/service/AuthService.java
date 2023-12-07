package com.chopvitebackend.chopvite.service;

import com.chopvitebackend.chopvite.controller.AuthController;
import com.chopvitebackend.chopvite.dto.*;
import com.chopvitebackend.chopvite.entity.UserEntity;
import com.chopvitebackend.chopvite.enums.Role;
import com.chopvitebackend.chopvite.repository.UserRepository;
import com.chopvitebackend.chopvite.security.JwtProvider;
import com.chopvitebackend.chopvite.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.NotAcceptableStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;

@Service
public class AuthService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private JwtProvider jwtProvider;

    @Autowired
    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository, JwtProvider jwtProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtProvider = jwtProvider;
    }


    public URI registerUser(RegisterDto registerDto, Role userRole) {
        try {


            validateUniqueEmail(registerDto.getEmail());

            UserEntity user = new UserEntity();
            user.setEmail(registerDto.getEmail());

//            user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
            String encodedPassword = passwordEncoder.encode(registerDto.getPassword());
            user.setPassword(encodedPassword);

            LOGGER.info("Encoded Password for {}: {}", registerDto.getEmail(), encodedPassword);

            user.setRole(userRole);

            user.setFirstName(registerDto.getFirstName());
            user.setLastName(registerDto.getLastName());
            user.setPhoneNumber(registerDto.getPhoneNumber());
            user.setBiometrics(registerDto.getBiometrics());
            user.setFaceVerification(registerDto.getFaceVerification());
            user.setProvider(registerDto.getProvider());
            user.setVehicleUrl(registerDto.getVehicleUrl());
            user.setInsuranceUrl(registerDto.getInsuranceUrl());
            user.setDriversLicenceUrl(registerDto.getDriversLicenceUrl());
            user.setSsn(registerDto.getSsn());
            user.setRestaurantName(registerDto.getRestaurantName());
            user.setRestaurantAddress(registerDto.getRestaurantAddress());
            user.setTypeOfBusiness(registerDto.getTypeOfBusiness());
            user.setIdentity(registerDto.getIdentity());

            user.setTimeCreated(LocalDateTime.now());

            UserEntity result = userRepository.save(user);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentContextPath().path("/user/me")
                    .buildAndExpand(result.getId()).toUri();

            return location;

        } catch (Exception e) {
            LOGGER.error("Error registering user: {}", e.getMessage(), e);
            throw e;
        }
    }

    private void validateUniqueEmail(String email) {
        try {
            if (StringUtil.isNotBlank(email) && userRepository.existsByEmail(email)) {
                LOGGER.info(String.format("Email address already in use: %s", email));
                throw new NotAcceptableStatusException("Email address already in use.");
            }
        } catch (Exception e) {
            LOGGER.error("Error validating unique email: {}", e.getMessage(), e);
            throw e;
        }
    }

    public AuthResponseDTO authenticateUser(LoginDto loginDto) {
        try {
            Authentication authentication = null;
            String token = "";

            if (StringUtil.isNotBlank(loginDto.getEmail())) {
                if (userRepository.existsByEmail(loginDto.getEmail())) {
                    LOGGER.info("Attempting to authenticate user: {}", loginDto.getEmail());

                    try {

                        authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                        loginDto.getEmail(),
                                        loginDto.getPassword()
                                )
                        );
                    } catch (BadCredentialsException e) {
                        // Log details to help diagnose the issue
                        LOGGER.error("Bad credentials for user: {}", loginDto.getEmail(), e);
                        throw e; // rethrow the exception
                    } catch (Exception e) {
                        LOGGER.error("An unexpected error occurred during authentication", e);
                        throw e; // rethrow the exception
                    }

                    LOGGER.info("User authenticated successfully: {}", loginDto.getEmail());

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    UserEntity user = userRepository.findByEmail(loginDto.getEmail()).get();
                    UserResponse userResponse = getUserResponseFromUser(user);
                    updateLastLogin(user);

                    token = jwtProvider.generateToken(authentication);
                    return new AuthResponseDTO(token, userResponse);
                } else {
                    LOGGER.error("User not found for email: {}", loginDto.getEmail());
                    throw new UsernameNotFoundException("User not found.");
                }
            }

            LOGGER.error("Invalid credentials. Email is blank.");
            throw new BadCredentialsException("Invalid credentials.");
        } catch (Exception e) {
            LOGGER.error("Error authenticating user: {}", e.getMessage(), e);
            throw e;
        }
    }

    private void updateLastLogin(UserEntity user) {
        try{
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        } catch (Exception e) {
            LOGGER.error("Error updating last login: {}", e.getMessage(), e);
            throw e;
        }
    }


    private UserResponse getUser(UserEntity user) {
        return getUserResponseFromUser(user);
    }

    private UserResponse getUserResponseFromUser(UserEntity user) {
        UserResponse userResponse = new UserResponse();
        try {
            BeanUtils.copyProperties(user, userResponse);
        } catch (Exception exception) {
            LOGGER.error("Error copying user properties", exception);
        }
        return userResponse;
    }


}
