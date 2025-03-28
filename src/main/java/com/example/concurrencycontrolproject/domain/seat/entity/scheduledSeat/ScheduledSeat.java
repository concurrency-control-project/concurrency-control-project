package com.example.concurrencycontrolproject.domain.seat.entity.scheduledSeat;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "ScheduledSeat", timeToLive = 10800)  // TTL 3시간
public class ScheduledSeat {
	@Id
	private String id;  // Redis에서는 ID를 String 타입으로 사용
	private Long scheduleId;
	private Long seatId;
	private Boolean isAssigned;
	private Long reservedBy; // 예약한 사용자 ID

}


