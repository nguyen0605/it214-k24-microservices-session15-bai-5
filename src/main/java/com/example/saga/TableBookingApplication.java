package com.example.saga;

import com.example.saga.entity.TableEntity;
import com.example.saga.model.BookingTransaction;
import com.example.saga.model.TableStatus;
import com.example.saga.repository.TableRepository;
import com.example.saga.service.TableBookingStateMachineImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TableBookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(TableBookingApplication.class, args);
    }

    @Bean
    CommandLineRunner init(TableRepository tableRepository, TableBookingStateMachineImpl stateMachine) {
        return args -> {
            TableEntity table = new TableEntity();
            table.setTableNumber("B7");
            table.setStatus(TableStatus.AVAILABLE);
            tableRepository.save(table);

            BookingTransaction tx = new BookingTransaction();
            tx.setBookingId("REST-2026-101");
            tx.setTableNumber("B7");
            tx.setDepositAmount(500000);

            stateMachine.process(tx, true);
        };
    }
}