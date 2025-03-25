package com.example.concurrencycontrolproject.domain.seat.service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.concurrencycontrolproject.domain.seat.entity.ScheduledSeat;
import com.example.concurrencycontrolproject.domain.seat.repository.ScheduledSeatRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScheduledSeatService {
	private final RedissonClient redissonClient;
	private final RedisTemplate<String, Object> redisTemplate;
	private final ScheduledSeatRepository scheduledSeatRepository;

	private static final String REDIS_KEY_PREFIX = "scheduled_seat:";

	public boolean reserveSeat(Long scheduleId, Long seatId, Long userId) {
		String key = REDIS_KEY_PREFIX + scheduleId + ":" + seatId;
		RLock lock = redissonClient.getLock(key + ":lock");

		try {
			if (!lock.tryLock(5, 10, TimeUnit.SECONDS)) {
				throw new RuntimeException("현재 좌석 예약이 많아 잠시 후 다시 시도해주세요.");
			}

			// Redis에서 좌석 상태 조회
			Map<Object, Object> seatData = redisTemplate.opsForHash().entries(key);
			if (!seatData.isEmpty() && Boolean.TRUE.equals(seatData.get("is_assigned"))) {
				throw new RuntimeException("이미 예약된 좌석입니다.");
			}

			// 예약 상태 업데이트
			redisTemplate.opsForHash().put(key, "is_assigned", true);
			redisTemplate.opsForHash().put(key, "reserved_by", userId);
			redisTemplate.expire(key, 2, TimeUnit.HOURS); // 2시간 후 자동 삭제

			return true;
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
	}

	@Transactional
	public void saveToMySQL(Long scheduleId, Long seatId) {
		ScheduledSeat scheduledSeat = new ScheduledSeat(null, scheduleId, seatId, true);
		scheduledSeatRepository.save(scheduledSeat);
	}
}
