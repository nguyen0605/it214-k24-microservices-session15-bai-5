package com.example.saga.service;

import com.example.saga.entity.TableEntity;
import com.example.saga.model.TableStatus;
import com.example.saga.repository.TableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TableReservationService {
    
    private final TableRepository tableRepository;
    
    public boolean reserveTable(String tableNumber, String bookingId) {
        TableEntity table = tableRepository.findByTableNumber(tableNumber);
        if (table != null && table.getStatus() == TableStatus.AVAILABLE) {
            table.setStatus(TableStatus.RESERVED);
            table.setReservedBy(bookingId);
            table.setReservedAt(LocalDateTime.now());
            tableRepository.save(table);
            return true;
        }
        return false;
    }
    
    public void releaseTable(String tableNumber) {
        TableEntity table = tableRepository.findByTableNumber(tableNumber);
        if (table != null && table.getStatus() == TableStatus.RESERVED) {
            table.setStatus(TableStatus.AVAILABLE);
            table.setReservedBy(null);
            table.setReservedAt(null);
            tableRepository.save(table);
        }
    }
    
    @Scheduled(fixedDelay = 300000)
    public void autoReleaseReservedTables() {
        List<TableEntity> expiredTables = tableRepository.findByStatusAndReservedAtBefore(
            TableStatus.RESERVED, LocalDateTime.now().minusMinutes(5));
        for (TableEntity table : expiredTables) {
            releaseTable(table.getTableNumber());
            log.info("[TableService] Auto-release expired reservation for table {}", table.getTableNumber());
        }
    }
}