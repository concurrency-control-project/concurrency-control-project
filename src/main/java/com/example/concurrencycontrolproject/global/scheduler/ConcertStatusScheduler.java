package com.example.concurrencycontrolproject.global.scheduler;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.concurrencycontrolproject.domain.concert.service.ConcertStatusSchedulerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConcertStatusScheduler {

	private final ConcertStatusSchedulerService concertStatusSchedulerService;

	@Scheduled(cron = "0 */15 * * * *")
	public void updateConcertsStatus() {
		try {
			log.info("콘서트 상태 갱신 스케줄러 실행");
			concertStatusSchedulerService.updateConcertsStatus(LocalDateTime.now());
		} catch (Exception e) {
			log.error("콘서트 상태 갱신 중 에러 발생", e);
		}
	}
}
