package com.example.saga.model;

import lombok.Data;

@Data
public class BookingTransaction {
    private String bookingId;
    private String tableNumber;
    private long depositAmount;
    private BookingState currentState;
}