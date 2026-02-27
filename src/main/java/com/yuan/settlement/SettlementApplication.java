package com.yuan.settlement;

import com.yuan.settlement.domain.PaymentRawData;
import com.yuan.settlement.repository.PaymentRepository;
import com.yuan.settlement.service.SettlementService;
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
	public CommandLineRunner run(SettlementService settlementService) {
		return args -> {
			System.out.println("================================");
			System.out.println("🚀 정산 프로세스 시작!");

			// 1. 정산 서비스의 로직을 호출합니다.
			settlementService.processSettlement();

			System.out.println("✅ 모든 정산 처리가 완료되었습니다.");
			System.out.println("================================");
		};
	}
}