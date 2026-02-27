package com.yuan.settlement.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String storeId;    //상점 고유 id (예 : STORE_A)
    private String name;        //상점 이름
    private BigDecimal feeRate; //수수료율 (예 :  0.03)

    @Builder
    public Store(String storeId, String name, BigDecimal feeRate) {
        this.storeId = storeId;
        this.name = name;
        this.feeRate = feeRate;
    }
}

