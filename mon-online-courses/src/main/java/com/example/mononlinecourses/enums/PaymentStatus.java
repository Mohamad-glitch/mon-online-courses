package com.example.mononlinecourses.enums;

public enum PaymentStatus {

    pending,      // Payment created but not yet processed
    processing,   // Payment is being processed by the gateway
    completed,    // Payment successful
    failed,       // Payment attempt failed


}
