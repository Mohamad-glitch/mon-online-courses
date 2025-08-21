package com.example.mononlinecourses.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "user_sessions")
@Data
public class UserSessions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(nullable = false, name = "user_email")
    private String email;

    @Column(name = "accessed")
    private boolean used;

    @Column(name = "jwt_token")
    private String token;

    @Column(name = "created_at")
    private Date createdAt;

}
