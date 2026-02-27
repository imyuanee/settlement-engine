package com.yuan.settlement.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "settlement_result")
@Getter
@NoArgsConstructor
public class SettlementResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String storeId;
    private String orderId;
    private BigDecimal totalAmount;
    private BigDecimal fee;
    private BigDecimal settlementAmount;
    private LocalDateTime settledAt;

    @Builder
    public SettlementResult(String storeId, String orderId, BigDecimal totalAmount, BigDecimal fee, BigDecimal settlementAmount, LocalDateTime settledAt) {
        this.storeId = storeId;
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.fee = fee;
        this.settlementAmount = settlementAmount;
        this.settledAt = settledAt;
    }
}