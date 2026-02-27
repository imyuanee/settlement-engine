package com.yuan.settlement.service;

import com.yuan.settlement.domain.PaymentRawData;
import com.yuan.settlement.domain.SettlementResult;
import com.yuan.settlement.repository.PaymentRepository;
import com.yuan.settlement.repository.SettlementResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final PaymentRepository paymentRepository;
    private final SettlementResultRepository resultRepository;

    public void processSettlement() {
        // 1. 결제 원천 데이터 다 가져오기
        List<PaymentRawData> payments = paymentRepository.findAll();

        for (PaymentRawData payment : payments) {
            // 2. 수수료 계산 (예: 모든 상점 3% 통일, 나중에 상점별로 다르게 설정 가능)
            BigDecimal feeRate = new BigDecimal("0.03");
            BigDecimal fee = payment.getAmount().multiply(feeRate);
            BigDecimal settlementAmount = payment.getAmount().subtract(fee);

            // 3. 결과 객체 생성
            SettlementResult result = SettlementResult.builder()
                    .storeId(payment.getStoreId())
                    .orderId(payment.getOrderId())
                    .totalAmount(payment.getAmount())
                    .fee(fee)
                    .settlementAmount(settlementAmount)
                    .build();

            // 4. DB에 정산 결과 저장
            resultRepository.save(result);

            System.out.println(payment.getOrderId() + " 처리 완료: 정산금액 " + settlementAmount);
        }
    }
}