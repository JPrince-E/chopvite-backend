package com.chopvitebackend.chopvite.entity;

import com.chopvitebackend.chopvite.enums.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String biometrics;

    private String faceVerification;

    private String provider;

    private String vehicleUrl;

    private String insuranceUrl;

    private String driversLicenceUrl;

    private String ssn;

    private String restaurantName;

    private String restaurantAddress;

    private String typeOfBusiness;

    private String identity;

    LocalDateTime timeCreated;

    LocalDateTime timeUpdated;

    LocalDateTime lastLogin;
}
