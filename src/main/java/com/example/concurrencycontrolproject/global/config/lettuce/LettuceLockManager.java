package com.example.concurrencycontrolproject.global.config.lettuce;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LettuceLockManager { // Lettuce 를 사용하여 레디스에 락 명령을 보내는 컴포넌트

	// private static final Logger log = LoggerFactory.getLogger(LettuceLockManager.class);
	//
	// private final StringRedisTemplate redisTemplate; // 레디스 명령 실행 템플릿
	// private final RedisScript<Long> lockScript;     // 락 루아 스크립트 주입
	// private final RedisScript<Long> unlockScript;   // 언락 루아 스크립트 주입
	//
	// // 분산락 요청
	// public String tryLock(String lockKey, long leaseTime, TimeUnit timeUnit) {
	// 	// lockKey: 락으로 사용할 Redis 키
	// 	// leaseTime: 락 최대 점유 시간
	// 	// timeUnit: 시간 단위
	//
	// 	// 락 소유자 식별 고유 값 생성
	// 	String lockValue = UUID.randomUUID().toString() + "-" + Thread.currentThread().getId();
	// 	long leaseMillis = timeUnit.toMillis(leaseTime); // 시간 단위 => 밀리초
	//
	// 	// 락 루아 스크립트 실행
	// 	Long result = redisTemplate.execute(
	// 		lockScript,
	// 		Collections.singletonList(lockKey), // KEYS[1]: 락 키
	// 		lockValue,                          // ARGV[1]: 락 소유자 식별 값
	// 		String.valueOf(leaseMillis)         // ARGV[2]: 락 만료 시간
	// 	);
	//
	// 	// 스크립트 실행 결과 확인 => 성공 1, 실패 0
	// 	if (result != null && result == 1L) {
	// 		log.info("[Thread-{}] 락 획득 성공: Key='{}', Value='{}'", Thread.currentThread().getId(), lockKey, lockValue);
	// 		return lockValue; // 락 획득 성공 시 락 값 반환
	// 	} else {
	// 		log.warn("[Thread-{}] 락 획득 실패: Key='{}'", Thread.currentThread().getId(), lockKey);
	// 		return null; // 락 획득 실패 시 null 반환
	// 	}
	// }
	//
	// // 분산락 해제
	// public void unlock(String lockKey, String lockValue) {
	// 	// lockKey: 락으로 사용할 Redis 키
	// 	// lockValue: 락 획득 시 반환받은 락 식별 값
	//
	// 	if (lockValue == null) {
	// 		log.warn("[Thread-{}] 락 해제 시도 중, lockValue 없음: Key='{}'", Thread.currentThread().getId(), lockKey);
	// 		return; // lockValue 가 없으면 해제 시도 없이 넘김
	// 	}
	//
	// 	// 언락 루아 스크립트 실행
	// 	Long result = redisTemplate.execute(
	// 		unlockScript,
	// 		Collections.singletonList(lockKey), // KEYS[1] = lockKey
	// 		lockValue                           // ARGV[1] = lockValue
	// 	);
	//
	// 	// 스크립트 실행 결과 확인 => 성공 1, 실패 0
	// 	if (result != null && result == 1L) {
	// 		log.info("[Thread-{}] 락 해제 성공: Key='{}', Value='{}'", Thread.currentThread().getId(), lockKey, lockValue);
	// 	} else {
	// 		// 0이 반환되는 경우
	// 		// => lockKey 가 존재하지 않음 => 이미 시간 만료로 자동 삭제되었거나, 다른 스레드가 먼저 해제
	// 		// => lockKey 는 존재하지만 lockValue 가 일치하지 않음 => 다른 스레드가 락을 점유 중
	// 		log.warn("[Thread-{}] 락 해제 실패: Key='{}', Value='{}', Result={}", Thread.currentThread().getId(),
	// 			lockKey, lockValue, result);
	// 	}
	// }
}