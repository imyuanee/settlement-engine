package com.yuan.settlement.domain;

import jakarta.persistence.*;
import lombok.Generated;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_raw_data")
@Getter
@NoArgsConstructor
public class PaymentRawData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;
    private String storeId;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;
}
