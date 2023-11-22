package com.chopvitebackend.chopvite.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserResponse {

    private String email;

    private String roles;

    private String roleName;

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
}
