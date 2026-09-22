package com.example.saga.entity;

import com.example.saga.model.TransactionType;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transaction")
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String bookingId;
    private long amount;
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    private String status;
    private LocalDateTime timestamp;
}