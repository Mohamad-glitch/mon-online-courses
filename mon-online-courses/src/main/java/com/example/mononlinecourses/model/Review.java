package com.example.mononlinecourses.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "reviews")
@Data
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "comment")
    private String comment;

    @Column(name = "rating")
    private int rating;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.DETACH})
    @JoinColumn(name = "course_id")
    private Course course;


    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.DETACH})
    @JoinColumn(name = "user_id")
    private User user;
}
