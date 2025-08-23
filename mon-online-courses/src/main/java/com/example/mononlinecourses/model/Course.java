package com.example.mononlinecourses.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "courses")
@Data
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "duration_minutes")
    private long durationMinutes;

    @Column(name = "rating_count")
    private long ratingCount;

    @Column(name = "rating_average")
    private double ratingAverage;

    @Column(name = "enrolled_count")
    private long enrolledCount;

    @Column(name = "language", nullable = false)
    private String language;

    @Column(name = "thumbnail", nullable = false)
    private String thumbnailUrl;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;


    @ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.DETACH})
    @JoinTable(
            name = "course_tags",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags;


    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.DETACH})
    @JoinColumn(name = "instructor_id")
    private User instructor;


    @ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.DETACH})
    @JoinTable(
            name = "course_categories",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Categorise> categorises;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "course")
    private List<Section> sections;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "course")
    private List<Enrollment> enrollments;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "course")
    private List<Wishlist> wishlists;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "course")
    private List<InstructorPayment> instructorPayments;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "course")
    private List<Payment> coursePayments;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "course")
    private List<Review> courseReviews;

}
