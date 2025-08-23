package com.example.mononlinecourses.repository;

import com.example.mononlinecourses.enums.Roles;
import com.example.mononlinecourses.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserDao extends JpaRepository<User, UUID> {
    Optional<User> findUsersByEmail(String email);

    boolean existsByEmailAndRole(String email, Roles role);

}
