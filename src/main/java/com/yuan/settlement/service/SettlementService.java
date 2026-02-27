package com.yuan.settlement.service;

import com.yuan.settlement.domain.PaymentRawData;
import com.yuan.settlement.domain.SettlementResult;
import com.yuan.settlement.domain.Store;
import com.yuan.settlement.repository.PaymentRepository;
import com.yuan.settlement.repository.SettlementResultRepository;
import com.yuan.settlement.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final PaymentRepository paymentRepository;
    private final SettlementResultRepository resultRepository;
    private final StoreRepository storeRepository;

    public void processSettlement() {
        // 1. 결제 원천 데이터 다 가져오기
        List<PaymentRawData> payments = paymentRepository.findAll();

        for (PaymentRawData payment : payments) {
            // 정보를 찾는데 없으면 에러 대신 Optional.empty()를 반환
            Store store = storeRepository.findByStoreId(payment.getStoreId())
                    .orElse(null);

            if (store == null) {
                System.out.println("⚠️ 상점 없음: " + payment.getStoreId() + "주문번호: " + payment.getOrderId());
                continue;
            }

            // 2. 수수료 계산 (상점에 설정된 수수료율로 계산)
            BigDecimal feeRate = store.getFeeRate();
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