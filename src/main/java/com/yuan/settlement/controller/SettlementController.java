package com.yuan.settlement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor // 생성자 주입을 자동으로 해줍니다
public class SettlementController {

    private final JobLauncher jobLauncher; // 배치 실행 도구
    private final Job settlementJob;      // BatchConfig에서 만든 Job

    @GetMapping("/run-batch")
    public String runBatch() {
        try {
            // 실행할 때마다 고유한 파라미터를 줘서 중복 실행 에러를 방지합니다.
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(settlementJob, jobParameters);
            return "🚀 정산 배치가 성공적으로 요청되었습니다! 콘솔 로그를 확인하세요.";
        } catch (Exception e) {
            return "❌ 에러 발생: " + e.getMessage();
        }
    }
}