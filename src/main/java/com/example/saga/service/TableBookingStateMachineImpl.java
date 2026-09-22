package com.example.saga.service;

import com.example.saga.model.BookingEvent;
import com.example.saga.model.BookingState;
import com.example.saga.model.BookingTransaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TableBookingStateMachineImpl {
    
    private final TableReservationService tableService;
    private final PaymentProcessingService paymentService;
    
    private void transition(String bookingId, BookingState nextState, BookingEvent event) {
        log.info("[Orchestrator] State transitioned to {} via event {}", nextState, event);
    }
    
    public void process(BookingTransaction transaction, boolean simulateFailureOnConfirm) {
        String bookingId = transaction.getBookingId();
        String tableNumber = transaction.getTableNumber();
        long amount = transaction.getDepositAmount();
        
        // Step 1: Initiated -> Table Reserving
        transaction.setCurrentState(BookingState.INITIATED);
        log.info("[Orchestrator] State: INITIATED -> Event: RESERVE_TABLE -> New State: TABLE_RESERVING");
        
        boolean locked = tableService.reserveTable(tableNumber, bookingId);
        if (!locked) {
            handleTableUnavailable(transaction);
            return;
        }
        log.info("[TableService] Table {}: AVAILABLE -> RESERVED (Semantic Lock acquired)", tableNumber);
        log.info("[TableService] Table {} reserved for booking {}", tableNumber, bookingId);
        
        // Step 2: Table Reserved -> Payment Pending -> Payment Completed
        transaction.setCurrentState(BookingState.TABLE_RESERVED);
        log.info("[Orchestrator] State: TABLE_RESERVED -> Event: TABLE_RESERVED -> New State: PAYMENT_PENDING");
        
        transaction.setCurrentState(BookingState.PAYMENT_PENDING);
        paymentService.processPayment(bookingId, amount);
        
        transaction.setCurrentState(BookingState.PAYMENT_COMPLETED);
        log.info("[Orchestrator] State: PAYMENT_PENDING -> Event: PAYMENT_SUCCESS -> New State: PAYMENT_COMPLETED");
        
        // Step 3: Confirm Booking
        transaction.setCurrentState(BookingState.BOOKING_CONFIRMING);
        log.info("[Orchestrator] State: PAYMENT_COMPLETED -> Event: CONFIRM_BOOKING -> New State: BOOKING_CONFIRMING");
        
        if (simulateFailureOnConfirm) {
            log.error("[TableService] ERROR: Table {} is already taken by another booking!", tableNumber);
            log.warn("[Orchestrator] Table reservation failed for {} (Already taken).", tableNumber);
            handleTableUnavailable(transaction);
        } else {
            transaction.setCurrentState(BookingState.BOOKING_CONFIRMED);
            log.info("[Orchestrator] Final State: BOOKING_CONFIRMED for booking {}", bookingId);
        }
    }
    
    private void handleTableUnavailable(BookingTransaction transaction) {
        String bookingId = transaction.getBookingId();
        String tableNumber = transaction.getTableNumber();
        long amount = transaction.getDepositAmount();
        
        transaction.setCurrentState(BookingState.CANCELLED);
        log.info("[Orchestrator] State: BOOKING_CONFIRMING -> Event: TABLE_UNAVAILABLE -> New State: CANCELLED");
        log.info("[Orchestrator] Initiating Compensation: Calling refundPayment for booking {}...", bookingId);
        
        paymentService.refundPayment(bookingId, amount);
        log.info("[RefundActivity] Refund of {} VND processed for booking {}.", amount, bookingId);
        
        tableService.releaseTable(tableNumber);
        log.info("[TableService] Table {}: RESERVED -> AVAILABLE (Semantic Lock released)", tableNumber);
        log.info("[BookingService] Booking {} updated to CANCELLED.", bookingId);
        log.info("[Final State] Table {} is now AVAILABLE (semantic lock released).", tableNumber);
    }
}