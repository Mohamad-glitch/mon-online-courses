package com.example.mononlinecourses.model;

import com.example.mononlinecourses.enums.Roles;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, unique = true, name = "id")
    private UUID id;

    @Column(nullable = false, unique = true, name = "email")
    private String email;

    @Column(nullable = false, name = "password")
    private String password;

    @Column(nullable = false, name = "full_name")
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "role")
    private Roles role;

    @Column(name = "bio")
    private String bio;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_login_at")
    private Date lastLogin;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "instructor")
    private List<Course> courses;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Enrollment> userEnrollments;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Wishlist> userWishlist;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "instructor")
    private List<InstructorPayment> instructorPayments;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Payment> userPayments;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Review> userReviews;


}
