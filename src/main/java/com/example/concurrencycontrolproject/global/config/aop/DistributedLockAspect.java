package com.example.concurrencycontrolproject.global.config.aop;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAspect { // @DistributedLock 어노테이션이 붙은 메서드 실행 전후로 락 획득 해제 로직을 수행

	private static final Logger log = LoggerFactory.getLogger(DistributedLockAspect.class);

	private final RedissonClient redissonClient; // 레디슨 주입

	// 어노테이션에 정의된 표현식을 실제 값으로 변환하는 도구 (SpEL)
	private final ExpressionParser expressionParser = new SpelExpressionParser();
	private final StandardReflectionParameterNameDiscoverer parameterNameDiscoverer = new StandardReflectionParameterNameDiscoverer();

	// @DistributedLock 붙은 메서드를 실행 전후에 개입
	@Around("@annotation(distributedLock)")
	public Object applyLock(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		Method method = signature.getMethod();
		Object[] args = joinPoint.getArgs();

		// SpEL로 동적 키 생성
		// 받은 인자 가지고 다이렉트로 다이나믹 동적 키 생성
		String dynamicKey = generateDynamicKey(distributedLock.keySuffixExpression(), method, args);

		// keyPrefix + 동적 키 가지고 락 키 생성
		String lockKey = distributedLock.keyPrefix() + ":" + dynamicKey;

		// 락 획득 시도 => tryLock
		// Redis 에서 락 객체(RLock) 호출
		RLock lock = redissonClient.getLock(lockKey);
		log.info("락 획득 시도: {}", lockKey);

		try {
			// @DistributedLock 에서 설정한 시간 관련 값
			boolean isLocked = lock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(),
				distributedLock.timeUnit());

			// 락 획득 실패 시 처리
			if (!isLocked) {
				log.warn("락 획득 실패: {}", lockKey);
				// 정해진 시간(waitTime) 동안 락을 얻지 못함 -> 예외 발생시켜 메서드 실행 중단
				throw new RuntimeException("획득 실패한 락: " + lockKey);
			}

			// 락 획득 성공 시 처리
			log.info("락 획득 성공: {}", lockKey);
			// AOP 가 적용된 원래 메서드 실행 => 이 메서드의 반환값이 applyLock 메서드의 최종 반환값이 됨
			return joinPoint.proceed();

			// 락 획득 대기 중에 스레드가 중단(interrupt)될 경우의 처리
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("락 획득 중단", e);
		} finally {
			// 작업 완료 후 최종적으로 락 해제 (락 반환) => finally 블록으로 오류가 발생하든, 성공하든 무조건 락 해제
			if (lock != null && lock.isHeldByCurrentThread()) {
				// lock.isHeldByCurrentThread(): 현재 이 코드를 실행하는 스레드가 실제로 락을 점유하고 있는지 확인 => 락이 없는데 unlock 호출하는 것 방지
				lock.unlock();
				log.info("락 해제 성공: {}", lockKey);
			}

		}

	}

	// 동적 키 생성 메서드 (SpEL)
	private String generateDynamicKey(String expression, Method method, Object[] args) {
		EvaluationContext context = new StandardEvaluationContext(); // SpEL을 실행할 컨텍스트 생성
		String[] paramNames = parameterNameDiscoverer.getParameterNames(method); // 메서드의 파라미터 이름 추출

		// 메서드 파라미터 이름과 값을 SpEL 컨텍스트에 등록 =>  이렇게 해야 SpEL 에서 파라미터 이름을 변수처럼 사용가능
		if (paramNames != null) {
			for (int i = 0; i < paramNames.length; i++) {
				context.setVariable(paramNames[i], args[i]);
			}
		}

		try {
			// SpEL 표현식을 파싱 + 컨텍스트를 기반으로 값 추출
			// 만약 expression = "#scheduleSeatId" 이고
			// context 에 scheduleSeatId=100 이 있다면 100 반환
			Object value = expressionParser.parseExpression(expression).getValue(context);
			return value != null ? value.toString() : ""; // 결과를 String 문자열로 반환
		} catch (Exception e) {
			log.error("SpEL 표현식 평가 오류: {}", expression, e);
			throw new RuntimeException("표현식에서 락 생성 실패: " + expression, e);
		}
	}

}