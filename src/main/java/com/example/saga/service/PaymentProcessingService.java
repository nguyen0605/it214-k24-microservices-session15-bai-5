package com.example.saga.service;

import com.example.saga.entity.Transaction;
import com.example.saga.model.TransactionType;
import com.example.saga.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentProcessingService {
    
    private final TransactionRepository transactionRepository;
    
    public void processPayment(String bookingId, long amount) {
        Transaction tx = new Transaction();
        tx.setBookingId(bookingId);
        tx.setAmount(amount);
        tx.setType(TransactionType.PAYMENT);
        tx.setStatus("PROCESSED");
        tx.setTimestamp(LocalDateTime.now());
        transactionRepository.save(tx);
        log.info("[PaymentService] Payment of {} VND processed for booking {}", amount, bookingId);
    }
    
    public void refundPayment(String bookingId, long amount) {
        Transaction refundTx = new Transaction();
        refundTx.setBookingId(bookingId);
        refundTx.setAmount(amount);
        refundTx.setType(TransactionType.REFUND);
        refundTx.setStatus("PROCESSED");
        refundTx.setTimestamp(LocalDateTime.now());
        transactionRepository.save(refundTx);
        log.info("[Transaction] Created REFUND record: +{} VND for booking {} (Audit Trail)", amount, bookingId);
    }
}