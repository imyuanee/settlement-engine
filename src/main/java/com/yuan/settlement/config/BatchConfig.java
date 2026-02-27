package com.yuan.settlement.config;

import com.yuan.settlement.domain.PaymentRawData;
import com.yuan.settlement.domain.SettlementResult;
import com.yuan.settlement.domain.Store;
import com.yuan.settlement.repository.PaymentRepository;
import com.yuan.settlement.repository.SettlementResultRepository;
import com.yuan.settlement.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final PaymentRepository paymentRepository;
    private final StoreRepository storeRepository;
    private final SettlementResultRepository resultRepository;

    // 1. Job: 전체 정산 작업의 이름표
    @Bean
    public Job settlementJob(JobRepository jobRepository, Step settlementStep) {
        return new JobBuilder("settlementJob", jobRepository)
                .start(settlementStep)
                .build();
    }

    // 2. Step: '읽기-가공-쓰기'가 실제로 일어나는 단계
    @Bean
    public Step settlementStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("settlementStep", jobRepository)
                .<PaymentRawData, SettlementResult>chunk(10, transactionManager) // 10개씩 묶어서 처리
                .reader(paymentReader())
                .processor(paymentProcessor())
                .writer(paymentWriter())
                .build();
    }

    // 3. Reader: DB에서 10개씩 페이징해서 읽어옴
    @Bean
    public RepositoryItemReader<PaymentRawData> paymentReader() {
        return new RepositoryItemReaderBuilder<PaymentRawData>()
                .name("paymentReader")
                .repository(paymentRepository)
                .methodName("findAll")
                .pageSize(10)
                .sorts(Map.of("id", Sort.Direction.ASC))
                .build();
    }

    // 4. Processor: 수수료 계산 로직 (유안 님이 짠 핵심 로직)
    @Bean
    public ItemProcessor<PaymentRawData, SettlementResult> paymentProcessor() {
        return payment -> {
            log.info("정산 처리 중인 주문번호: {}", payment.getOrderId());

            Store store = storeRepository.findByStoreId(payment.getStoreId())
                    .orElse(null);

            if (store == null) {
                log.warn("상점 정보가 없어 건너뜁니다: {}", payment.getStoreId());
                return null; // null 반환 시 해당 데이터는 정산 결과(Writer)에 포함 안 됨
            }

            BigDecimal fee = payment.getAmount().multiply(store.getFeeRate());

            return SettlementResult.builder()
                    .storeId(payment.getStoreId())
                    .orderId(payment.getOrderId())
                    .totalAmount(payment.getAmount())
                    .fee(fee)
                    .settlementAmount(payment.getAmount().subtract(fee))
                    .settledAt(LocalDateTime.now())
                    .build();
        };
    }

    // 5. Writer: 계산된 10개의 결과를 한 번에 DB에 저장
    @Bean
    public ItemWriter<SettlementResult> paymentWriter() {
        return items -> {
            log.info("배치 쓰기 실행: {}건 저장", items.size());
            resultRepository.saveAll(items);
        };
    }
}