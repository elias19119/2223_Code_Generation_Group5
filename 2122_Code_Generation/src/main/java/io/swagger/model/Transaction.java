package io.swagger.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Transaction {
    @Id
    @GeneratedValue
    private UUID id;

    private UUID userId;

    private String fromIban;

    private String toIban;

    private long transferAmount;

    private LocalDate dateOfTransaction;
}
