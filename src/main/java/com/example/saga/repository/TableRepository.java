package com.example.saga.repository;

import com.example.saga.entity.TableEntity;
import com.example.saga.model.TableStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface TableRepository extends JpaRepository<TableEntity, Long> {
    TableEntity findByTableNumber(String tableNumber);
    List<TableEntity> findByStatusAndReservedAtBefore(TableStatus status, LocalDateTime time);
}