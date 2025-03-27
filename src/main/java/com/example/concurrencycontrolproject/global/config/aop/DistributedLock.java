package com.example.concurrencycontrolproject.global.config.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock { // 락 적용 대상 메서드를 지정 + 락 키 생성 규칙 정의할 어노테이션 인터페이스

	String keyPrefix(); // 락 이름 => 그냥 임의로 정하는 락 별칭하는 것

	String keySuffixExpression(); // 락 키의 동적 부분을 생성하기 위한 SpEL 표현식 => 락 걸 때 참조할 값

	// 시간 관련 => 어노테이션 적용할 때, 추가로 설정할 수 있지만 안적으면 여기서 설정한 기본 값으로 작동
	long waitTime() default 5000;  // 획득을 시도할 최대 시간 => 이 시간 동안 락을 얻지 못하면 실패

	long leaseTime() default 5000; // 락을 점유할 최대 시간 => 이 시간이 지나면 락이 자동으로 해제됨 => 데드락 방지

	TimeUnit timeUnit() default TimeUnit.MILLISECONDS; // 시간 단위 설정

}
