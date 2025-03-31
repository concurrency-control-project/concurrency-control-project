package com.example.concurrencycontrolproject.global.config.lettuce;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class LettuceDistributedLockAspect { // @LettuceDistributedLock 어노테이션이 붙은 메서드 실행 전후로 락 획득 해제 로직을 수행

	// private static final Logger log = LoggerFactory.getLogger(LettuceDistributedLockAspect.class);
	// private final LettuceLockManager lettuceLockManager; // LettuceLockManager 주입
	//
	// // 어노테이션에 정의된 표현식을 실제 값으로 변환하는 도구 (SpEL)
	// private final ExpressionParser expressionParser = new SpelExpressionParser();
	// private final StandardReflectionParameterNameDiscoverer parameterNameDiscoverer = new StandardReflectionParameterNameDiscoverer();
	//
	// // @LettuceDistributedLock 붙은 메서드를 실행 전후에 개입
	// @Around("@annotation(lettuceDistributedLock)")
	// public Object applyLock(ProceedingJoinPoint joinPoint,
	// 	LettuceDistributedLock lettuceDistributedLock) throws Throwable {
	//
	// 	MethodSignature signature = (MethodSignature)joinPoint.getSignature();
	// 	Method method = signature.getMethod();
	// 	Object[] args = joinPoint.getArgs();
	//
	// 	// SpEL로 동적 키 생성
	// 	// 받은 인자 가지고 다이렉트로 다이나믹 동적 키 생성
	// 	String dynamicKey = generateDynamicKey(lettuceDistributedLock.keySuffixExpression(), method, args);
	//
	// 	// keyPrefix + 동적 키 가지고 락 키 생성
	// 	String lockKey = lettuceDistributedLock.keyPrefix() + ":" + dynamicKey;
	//
	// 	String lockValue = null; // 락 획득 성공 시 Redis 에 저장된 락 소유 식별자 저장할 변수
	// 	boolean lockAcquired = false; // 락 획득 여부
	// 	boolean synchronizationRegistered = false; // 트랜잭션 동기화 등록 여부
	//
	// 	// 락 획득 시도 시간, 최대 점유 시간 설정 => 어노테이션 값 사용
	// 	long startTime = System.currentTimeMillis();
	// 	long waitMillis = lettuceDistributedLock.timeUnit().toMillis(lettuceDistributedLock.waitTime());
	// 	long leaseMillis = lettuceDistributedLock.timeUnit().toMillis(lettuceDistributedLock.leaseTime());
	//
	// 	log.info("[Thread-{}] 락 획득 시도 시작: Key='{}', WaitTime={}ms, LeaseTime={}ms",
	// 		Thread.currentThread().getId(), lockKey, waitMillis, leaseMillis);
	//
	// 	// 락 획득 시도
	// 	// Redisson 의 tryLock 은 내부적으로 대기 메커니즘을 가질 수 있지만
	// 	// => Lettuce 는 즉시 결과를 반환하므로 클라이언트 측에서 waitTime 만큼 재시도하는 로직 구현 => Spin Lock
	// 	// => 계속 스레드가 돌아가면서 CPU 를 점유하고 있음 => CPU 자원 낭비
	// 	while (!lockAcquired && (System.currentTimeMillis() - startTime) < waitMillis) {
	// 		// LettuceLockManager 를 사용하여 락 획득 시도 => 락 루아 스크립트 실행
	// 		lockValue = lettuceLockManager.tryLock(lockKey, lettuceDistributedLock.leaseTime(),
	// 			lettuceDistributedLock.timeUnit());
	//
	// 		// tryLock 성공 시 lockValue 반환, 실패 시 null 반환 => lockValue 가 null 이 아니면 획득 성공
	// 		lockAcquired = (lockValue != null);
	//
	// 		if (!lockAcquired) {
	// 			try {
	// 				// Spin Lock 방식 때문에 계속 스레드를 돌려야 해서 짧은 시간 대기 후 재시도
	// 				// => 너무 짧게 설정하면 Redis 부하 증가, 너무 길면 응답성 저하
	// 				Thread.sleep(50); // 50밀리초 대기
	// 			} catch (InterruptedException e) {
	// 				// 락 획득 대기 중에 스레드가 중단(interrupt)될 경우의 처리
	// 				Thread.currentThread().interrupt();
	// 				log.error("[Thread-{}] 락 획득 대기 중 인터럽트 발생: Key='{}'", Thread.currentThread().getId(), lockKey, e);
	// 				throw new RuntimeException("락 획득 중단됨", e);
	// 			}
	// 		}
	// 	}
	// 	// 락 획득 시도 종료 => 루프 종료
	//
	// 	log.info("[Thread-{}] 락 획득 결과: {}, Key='{}', AcquiredValue='{}'",
	// 		Thread.currentThread().getId(), lockAcquired, lockKey, lockValue);
	//
	// 	// 락 획득 실패 처리
	// 	if (!lockAcquired) {
	// 		log.warn("[Thread-{}] 락 획득 최종 실패 (타임아웃): Key='{}'", Thread.currentThread().getId(), lockKey);
	// 		throw new RuntimeException("락 획득 실패 (타임아웃): " + lockKey);
	// 	}
	//
	// 	// 락 획득 성공 시 처리 + 트랜잭션 동기화 적용
	// 	try {
	// 		if (TransactionSynchronizationManager.isActualTransactionActive()) {
	// 			log.info("[Thread-{}] 트랜잭션이 시작, 완료 후 락 해제 될 예정: Key='{}'", Thread.currentThread().getId(),
	// 				lockKey);
	//
	// 			// 콜백에서 사용하기 위해 final 변수로 복사 => 중간에 값이 변경되면 오류 발생
	// 			final String finalLockKey = lockKey;
	// 			final String finalLockValue = lockValue; // 해제 시 필요한 락 식별 값
	//
	// 			// 트랜잭션 완료 시 실행될 콜백 등록
	// 			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
	//
	// 				@Override
	// 				public void afterCompletion(int status) {
	//
	// 					// 트랜잭션 완료 후 실행
	// 					try {
	// 						// 트랜잭션 완료 후 락 해제 시도
	// 						// LettuceLockManager 를 사용하여 언락 루아 스크립트로 락 해제
	// 						// => 락 획득 시 받은 lockValue 를 함께 전달하여 자신이 획득한 락만 해제하도록 함
	// 						lettuceLockManager.unlock(finalLockKey, finalLockValue);
	// 						log.info("[Thread-{}] 트랜잭션 완료({}) 후 락 해제 호출됨: Key='{}'",
	// 							Thread.currentThread().getId(),
	// 							status == STATUS_COMMITTED ? "COMMIT" : "ROLLBACK/UNKNOWN",
	// 							finalLockKey); // 트랜잭션 상태 로깅
	//
	// 					} catch (Exception e) {
	//
	// 						// unlock 중에 발생할 수 있는 예외 처리 로깅
	// 						log.error("[Thread-{}] 트랜잭션 완료 후 락 해제 중 오류 발생: Key='{}'", Thread.currentThread().getId(),
	// 							finalLockKey, e);
	// 					}
	// 				}
	// 			});
	//
	// 			synchronizationRegistered = true; // 동기화 등록 표시
	// 		} else {
	// 			log.info("[Thread-{}] 활성 트랜잭션 없음. 메서드 종료 시 finally 블록에서 락 해제 예정: Key='{}'",
	// 				Thread.currentThread().getId(), lockKey);
	// 		}
	//
	// 		// AOP 가 적용된 원래 메서드 실행 => 이 메서드의 반환값이 applyLock 메서드의 최종 반환값이 됨
	// 		return joinPoint.proceed();
	//
	// 	} finally {
	// 		// 작업 완료 후 최종적으로 락 해제 (락 반환)
	// 		// => finally 블록으로 오류가 발생하든, 성공하든 무조건 락 해제
	// 		// + 추가로 락을 획득했고, 트랜잭션 동기화가 등록되지 않은 경우에만 락 해제 하도록 수정
	// 		// => 트랜잭션 없는 메서드는 여기서 락 해제됨
	//
	// 		if (lockAcquired && !synchronizationRegistered) {
	// 			try {
	// 				// LettuceLockManager 를 사용하여 언락 루아 스크립트로 락 해제
	// 				// => 락 획득 시 받은 lockValue 를 함께 전달하여 자신이 획득한 락만 해제하도록 함
	// 				lettuceLockManager.unlock(lockKey, lockValue);
	// 				log.info("[Thread-{}] 트랜잭션 없는 메서드 완료, finally 에서 락 해제 호출됨: Key='{}'",
	// 					Thread.currentThread().getId(), lockKey);
	// 			} catch (Exception e) {
	// 				log.error("[Thread-{}] 트랜잭션 없는 메서드 완료 후 finally 에서 락 해제 중 오류 발생: Key='{}'",
	// 					Thread.currentThread().getId(), lockKey, e);
	// 			}
	// 		}
	// 	}
	// }
	//
	// // generateDynamicKey 메서드는 기존 코드와 동일하게 사용
	// private String generateDynamicKey(String expression, Method method, Object[] args) {
	// 	EvaluationContext context = new StandardEvaluationContext();
	// 	String[] paramNames = parameterNameDiscoverer.getParameterNames(method);
	// 	if (paramNames != null) {
	// 		for (int i = 0; i < paramNames.length; i++) {
	// 			context.setVariable(paramNames[i], args[i]);
	// 		}
	// 	}
	// 	try {
	// 		Object value = expressionParser.parseExpression(expression).getValue(context);
	// 		return value != null ? value.toString() : "";
	// 	} catch (Exception e) {
	// 		log.error("SpEL 표현식 평가 오류: {}", expression, e);
	// 		throw new RuntimeException("표현식 기반 락 키 생성 실패: " + expression, e);
	// 	}
	// }
}
