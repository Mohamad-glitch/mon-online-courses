package com.example.mononlinecourses.model;

import com.example.mononlinecourses.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "instructor_payouts")
@Data
public class InstructorPayment {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "amount")
    private double amount;

    @Column(name = "payout_status")
    private PaymentStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "payout_date")
    private Date payoutDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.DETACH})
    @JoinColumn(name = "course_id")
    private Course course;


    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.DETACH})
    @JoinColumn(name = "instructor_id")
    private User instructor;
}
