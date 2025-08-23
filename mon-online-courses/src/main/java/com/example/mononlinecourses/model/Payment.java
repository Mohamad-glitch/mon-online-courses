package com.example.mononlinecourses.model;

import com.example.mononlinecourses.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.UUID;


@Entity
@Table(name = "payments")
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "amount")
    private double amount;

    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    @Column(name = "method")
    private String paymentMethod;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "transaction_date")
    private Date paymentDate;


    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.DETACH})
    @JoinColumn(name = "course_id")
    private Course course;


    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.DETACH})
    @JoinColumn(name = "user_id")
    private User user;
}
