package com.example.mononlinecourses.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "wishlists")
@Data
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "added_at")
    private Date addedAt;


    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.DETACH})
    @JoinColumn(name = "course_id")
    private Course course;


    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.DETACH})
    @JoinColumn(name = "user_id")
    private User user;
}
