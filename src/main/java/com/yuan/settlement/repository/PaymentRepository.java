package com.yuan.settlement.repository;

import com.yuan.settlement.domain.PaymentRawData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentRawData, Long> {

}
