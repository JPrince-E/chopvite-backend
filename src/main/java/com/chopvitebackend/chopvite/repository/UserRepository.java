package com.chopvitebackend.chopvite.repository;

import com.chopvitebackend.chopvite.entity.UserEntity;
import com.chopvitebackend.chopvite.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByEmail(String email);
    Boolean existsByEmail(String email);

    Optional<UserEntity> findByEmailAndRole(String email, Role userRole);
}
