package com.example.saga.entity;

import com.example.saga.model.TableStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "restaurant_table")
@Data
public class TableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tableNumber;
    @Enumerated(EnumType.STRING)
    private TableStatus status;
    private String reservedBy;
    private LocalDateTime reservedAt;
}