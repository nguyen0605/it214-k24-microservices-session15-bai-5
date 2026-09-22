package com.example.saga.model;

public enum BookingEvent {
    RESERVE_TABLE,
    TABLE_RESERVED,
    PROCESS_PAYMENT,
    PAYMENT_SUCCESS,
    CONFIRM_BOOKING,
    TABLE_UNAVAILABLE
}