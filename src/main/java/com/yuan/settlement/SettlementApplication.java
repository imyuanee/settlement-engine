package com.yuan.settlement;

import com.yuan.settlement.domain.PaymentRawData;
import com.yuan.settlement.repository.PaymentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class SettlementApplication {

	public static void main(String[] args) {
		SpringApplication.run(SettlementApplication.class, args);
	}

	@Bean
	public CommandLineRunner test(PaymentRepository paymentRepository) {
		return args -> {
			System.out.println("================================");
			System.out.println("DB 데이터 불러오기 테스트 시작!");

			// 1. Repository를 통해 DB의 모든 결제 데이터를 가져옴
			List<PaymentRawData> payments = paymentRepository.findAll();

			if (payments.isEmpty()) {
				System.out.println("⚠️ DB에 데이터가 없어요! DBeaver에서 데이터를 넣었는지 확인하세요.");
			} else {
				System.out.println("조회된 데이터 개수: " + payments.size());

				// 2. 가져온 데이터를 하나씩 출력
				payments.forEach(p -> {
					System.out.println("주문번호: " + p.getOrderId() + " | 금액: " + p.getAmount());
				});
			}

			System.out.println("================================");
		};
	}
}