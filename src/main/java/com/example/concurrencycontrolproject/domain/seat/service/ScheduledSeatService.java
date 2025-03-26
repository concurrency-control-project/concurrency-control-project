package com.example.concurrencycontrolproject.domain.seat.service;

import java.util.Collections;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import com.example.concurrencycontrolproject.domain.seat.entity.ScheduledSeat;
import com.example.concurrencycontrolproject.domain.seat.repository.ScheduledSeatRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduledSeatService {

	private final RedisTemplate<String, Object> redisTemplate;
	private final DefaultRedisScript<Long> redisScript;
	private final ScheduledSeatRepository scheduledSeatRepository;

	// 좌석 예약
	public boolean reserveSeat(Long scheduleId, Long seatId, Long userId) {
		String redisKey = "scheduled_seat:" + scheduleId + ":" + seatId;
		List<String> keys = Collections.singletonList(redisKey);
		Long result = redisTemplate.execute(redisScript, keys, userId.toString());

		if (result != null && result == 1) {
			// Redis에 예약 정보 저장
			ScheduledSeat scheduledSeat = new ScheduledSeat(redisKey, scheduleId, seatId, true, userId);
			scheduledSeatRepository.save(scheduledSeat);
			return true;
		}
		return false;
	}

	// 예약 취소
	public void cancelReservation(Long scheduleId, Long seatId) {
		String redisKey = "scheduled_seat:" + scheduleId + ":" + seatId;

		// Redis에서 예약 데이터 삭제
		scheduledSeatRepository.deleteById(redisKey);
		redisTemplate.delete(redisKey);
	}

	// 예약 상태 조회
	public ScheduledSeat getReservation(Long scheduleId, Long seatId) {
		String redisKey = "scheduled_seat:" + scheduleId + ":" + seatId;
		return scheduledSeatRepository.findById(redisKey).orElse(null);
	}
}


